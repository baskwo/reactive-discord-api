package rda;

import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;

@RequiredArgsConstructor
public class DiscordClient {
    private static final String DISCORD_URL = "https://discord.com/api";

    private final String token;

    public static void main(String[] args) {
        DiscordClient discordClient = new DiscordClient("NzU0Nzk5NjcyMzYyNzk1MDYw.X15_-Q.zlCwqhj6c6qJB4lI5KB-D6ZBA4c");

        discordClient.login();
    }

    public void login() {
        String response = HttpClient.create()
                .baseUrl(DISCORD_URL)
                .headers(headers -> headers.add(HttpHeaderNames.AUTHORIZATION, "Bot " + token))
                .get()
                .uri("/gateway/bot?v=6&encoding=json")
                .responseSingle((resp, bytes) -> {
                    System.out.println(resp.status().code());
                    return bytes.asString();
                })
                .block();

        System.out.println(response);

        String data = HttpClient.create()
                .baseUrl("wss://gateway.discord.gg/?v=6&encoding=json")
                .headers(headers -> headers.add(HttpHeaderNames.USER_AGENT, "DiscordBot ($url, $versionNumber)"))
                .websocket(WebsocketClientSpec.builder()
                        .maxFramePayloadLength(Integer.MAX_VALUE)
                        .build())
                .handle((inbound, outbound) -> inbound.aggregateFrames()
                        .receive()
                        .asString()
                ).blockLast();

        System.out.println(data);
    }
}
