package com.BE.service;

import com.BE.exception.exceptions.BadRequestException;
import com.BE.model.entity.Booking;
import com.BE.model.entity.User;
import com.BE.model.request.CreateGoogleMeetRequest;
import com.BE.service.interfaceServices.IBookingService;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoogleMeetService {


    private final AccountUtils accountUtils;
    private final DateNowUtils dateNowUtils;
    private final IBookingService bookingService;
    public GoogleMeetService(AccountUtils accountUtils,
                             DateNowUtils dateNowUtils,
                             IBookingService bookingService) {
        this.accountUtils = accountUtils;
        this.dateNowUtils = dateNowUtils;
        this.bookingService = bookingService;
    }

    private static final String TOKENS_DIRECTORY_PATH = "src/main/resources";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Value("${google.calendar.client.id}")
    private String clientId;
    @Value("${google.calendar.client.secret}")
    private String clientSecret;
    @Value("${google.calendar.redirect.uri}")
    private String redirectUri;
    @Value("${google.calendar.application.name}")
    private String applicationName;
    @Value("${google.calendar.scope}")
    private String scope;
    @Value("${google.calendar.access.type}")
    private String accessType;


    private Calendar googleCalendarService(Credential credential) throws GeneralSecurityException, IOException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(applicationName)
                .build();
    }

    public String createGoogleMeetLink(CreateGoogleMeetRequest request){
        User user = accountUtils.getCurrentUser();
        Booking booking = bookingService.getBookingById(request.getBookingId());
        try {
            Credential credential = getStoredCredential(user.getEmail());
            if (credential == null) {
                // Nếu không có credential, yêu cầu người dùng đăng nhập
                String authorizationUrl = getAuthorizationUrl();
                throw new BadRequestException("Chưa có thông tin chứng thực, cần đăng nhập trước. Truy cập vào URL sau: "
                        + authorizationUrl);
            }

            Calendar service = googleCalendarService(credential);
            Event event = new Event()
                    .setSummary(request.getSummary())
                    .setDescription(request.getDescription());

            // Set start and end time
            DateTime startDateTime = dateNowUtils.convertLocalDateTimeToDateTime(booking.getTimeFrame().getTimeFrameFrom());
            DateTime endDateTime = dateNowUtils.convertLocalDateTimeToDateTime(booking.getTimeFrame().getTimeFrameTo());

            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh");

            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Asia/Ho_Chi_Minh");

            event.setStart(start);
            event.setEnd(end);

            // Enable Google Meet link
            String requestId = UUID.randomUUID().toString();
            ConferenceData conferenceData = new ConferenceData()
                    .setCreateRequest(new CreateConferenceRequest()
                            .setRequestId(requestId) // Must be unique
                            .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet")));
            event.setConferenceData(conferenceData);

            // Set attendees
            List<String> attendees = request.getAttendees();
            event.setAttendees(attendees.stream()
                    .map(email -> new EventAttendee().setEmail(email))
                    .collect(Collectors.toList()));


            // Insert the event into the user's primary calendar
            Event createdEvent = service.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();
            booking.setMeetLink(createdEvent.getConferenceData().getEntryPoints().get(0).getUri());
            bookingService.saveBooking(booking);
            return createdEvent.getConferenceData().getEntryPoints().get(0).getUri();

        } catch (GeneralSecurityException | IOException e) {
            throw new BadRequestException("Không thể tạo link Meet");
        }
    }

    public String getAuthorizationUrl() throws GeneralSecurityException, IOException {
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                Arrays.asList("https://www.googleapis.com/auth/calendar", "https://www.googleapis.com/auth/userinfo.email"))
                .setAccessType(accessType)
                .build();
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    private String getUserEmailFromToken(String accessToken) throws IOException, GeneralSecurityException {
        HttpRequestFactory requestFactory = GoogleNetHttpTransport.newTrustedTransport().createRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(
                new GenericUrl("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken));
        HttpResponse response = request.execute();

        JsonObject jsonObject = JsonParser.parseString(response.parseAsString()).getAsJsonObject();
        return jsonObject.get("email").getAsString();
    }

    public String exchangeCodeForToken(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    Arrays.asList("https://www.googleapis.com/auth/calendar", "https://www.googleapis.com/auth/userinfo.email"))
                    .setAccessType(accessType)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .build();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            // Retrieve user's email using the access token
            String userEmail = getUserEmailFromToken(tokenResponse.getAccessToken());

            // Use the user's email as the key when storing credentials
            flow.createAndStoreCredential(tokenResponse, userEmail);
            return userEmail;
        } catch (IOException | GeneralSecurityException e) {
            throw new BadRequestException("Không thể đăng nhập");
        }
    }


    public Credential getStoredCredential(String userEmail) throws IOException, GeneralSecurityException {
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));
        DataStore<StoredCredential> dataStore = dataStoreFactory.getDataStore("StoredCredential");

        if (dataStore.containsKey(userEmail)) {
            // Sử dụng Builder để tạo Credential với đầy đủ thông tin cần thiết
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build();

            // Set access token và refresh token từ file lưu trữ
            credential.setAccessToken(dataStore.get(userEmail).getAccessToken());
            credential.setRefreshToken(dataStore.get(userEmail).getRefreshToken());

            // Kiểm tra và làm mới token nếu cần
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
                credential.refreshToken();
            }
            return credential;
        } else {
            return null;
        }
    }



}
