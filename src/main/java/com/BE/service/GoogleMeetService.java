package com.BE.service;

import com.BE.enums.BookingTypeEnum;
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

    public GoogleMeetService(AccountUtils accountUtils,
                             DateNowUtils dateNowUtils
                          ) {
        this.accountUtils = accountUtils;
        this.dateNowUtils = dateNowUtils;

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
    private static final String CREDENTIALS_FILE = "StoredCredentials";
    private static final String USER_ROOT = "userroot";

    private Calendar googleCalendarService(Credential credential) throws GeneralSecurityException, IOException {
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
                .setApplicationName(applicationName)
                .build();
    }

    public String createGoogleMeetLink(Booking booking){

        try {
            Credential credential = getStoredCredential();
            if (credential == null) {
                throw new BadRequestException("Token not found");
            }
            Calendar service = googleCalendarService(credential);

            Event event = new Event()
                    .setSummary("Mentor Bridge Booking")
                    .setDescription("Mentor: " + booking.getMentor().getFullName());

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

            // Insert the event into the user's primary calendar
            Event createdEvent = service.events().insert("primary", event)
                    .setConferenceDataVersion(1)
                    .execute();

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
                Collections.singleton(scope))
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

    public void exchangeCodeForToken(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientId,
                    clientSecret,
                    Collections.singleton(scope))
                    .setAccessType(accessType)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .build();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            // Use the user's email as the key when storing credentials
            flow.createAndStoreCredential(tokenResponse, USER_ROOT);
        } catch (IOException | GeneralSecurityException e) {
            throw new BadRequestException("Không thể đăng nhập");
        }
    }


    public Credential getStoredCredential() throws IOException, GeneralSecurityException {
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));
        DataStore<StoredCredential> dataStore = dataStoreFactory.getDataStore(CREDENTIALS_FILE);

        if (dataStore.containsKey(USER_ROOT)) {
            // Sử dụng Builder để tạo Credential với đầy đủ thông tin cần thiết
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setJsonFactory(JSON_FACTORY)
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                    .setTokenServerUrl(new GenericUrl("https://oauth2.googleapis.com/token"))
                    .build();

            // Set access token và refresh token từ file lưu trữ
            credential.setAccessToken(dataStore.get(USER_ROOT).getAccessToken());
            credential.setRefreshToken(dataStore.get(USER_ROOT).getRefreshToken());

            // Kiểm tra và làm mới token nếu cần
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60 ) {
                credential.refreshToken();
            }
            return credential;
        } else {
            return null;
        }
    }



}
