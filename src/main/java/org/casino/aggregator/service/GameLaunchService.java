package org.casino.aggregator.service;

import org.casino.aggregator.dto.AggregatorLaunchRequestDTO;
import org.casino.aggregator.dto.GameLaunchRequestDTO;
import org.casino.aggregator.dto.GameLaunchResponse;
import org.casino.aggregator.exception.UnknownGameIdException;
import org.casino.aggregator.util.SecurityUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.Base64;
import java.io.File;


@Service
public class GameLaunchService {

    public static Properties properties;

    public GameLaunchService() {
        properties = fetchProperties();
    }

    /**
     * Construct the provider payload and call the relevant webservice to get the final gameLaunch URL and return it.
     * @param aggregatorLaunchRequestDTO object containing the gameId and token
     * @return the game launch url
     */
    public String getGameLaunchUrl(AggregatorLaunchRequestDTO aggregatorLaunchRequestDTO) throws UnknownGameIdException {

        GameLaunchRequestDTO gameLaunchRequestDTO  = new GameLaunchRequestDTO();
        gameLaunchRequestDTO.setParams(aggregatorLaunchRequestDTO.getToken(), aggregatorLaunchRequestDTO.getGameid());

        if (aggregatorLaunchRequestDTO.getGameid().equals("superspins")) {
            String messageToSign = aggregatorLaunchRequestDTO.getToken()+"|"+aggregatorLaunchRequestDTO.getGameid();
            gameLaunchRequestDTO.setSignature(SecurityUtil.signMessage(messageToSign,properties.getProperty("sign.keyfile.path")));
        }

        return getGameLaunchUrlFromProvider(gameLaunchRequestDTO);
    }

    private String getGameLaunchUrlFromProvider(GameLaunchRequestDTO gameLaunchRequestDTO) throws UnknownGameIdException {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<GameLaunchResponse> response;
        try {
            String wsUrl = properties.getProperty("game.provider." + gameLaunchRequestDTO.getParams().getGameid() + ".url");
            if (wsUrl == null) {
                throw new UnknownGameIdException(gameLaunchRequestDTO.getParams().getGameid() + " - GameId unknown.");
            }
            HttpHeaders headers = createHttpHeadersWithBasicAuth();
            HttpEntity<GameLaunchRequestDTO> requestBody = new HttpEntity<>(gameLaunchRequestDTO, headers);
            response = restTemplate.exchange(wsUrl, HttpMethod.POST, requestBody, GameLaunchResponse.class);
        } catch (RestClientException ex) {
            System.out.println("Exception: " + ex.getMessage());
            throw new RuntimeException("Unable to get game launch url.");
        }

        return Objects.requireNonNull(response.getBody()).getMessage();
    }

    private HttpHeaders createHttpHeadersWithBasicAuth()
    {
        HttpHeaders headers = new HttpHeaders();
        String encodedString = Base64.getEncoder()
                .encodeToString(
                        (properties.getProperty("game.provider.api.user") + ":" + properties.getProperty("game.provider.api.password")).getBytes());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedString);
        return headers;
    }

    private static Properties fetchProperties(){
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile("classpath:gameproviders.properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return properties;
    }
}
