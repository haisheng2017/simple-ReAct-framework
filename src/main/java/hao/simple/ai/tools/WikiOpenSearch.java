package hao.simple.ai.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hào on 2024/8/21
 */
public class WikiOpenSearch {
    //    https://www.mediawiki.org/wiki/Special:English/API:Opensearch
    private static final String URL = "https://en.wikipedia.org/w/api.php";

    private final HttpClient client;
    private final Map<String, String> defaultParam = Map.of("action", "opensearch", "limit", "5", "format", "json");

    public WikiOpenSearch() {
        this.client = HttpClient.newBuilder()
                .build();
    }

    public String search(String keyword) throws IOException, InterruptedException {
        for (var k : keyword.split(" ")) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "?" + toQueryParam(URLEncoder.encode(k, StandardCharsets.UTF_8))))
                    .GET()
                    .build();
            HttpResponse<String> s = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(s.body());
        }

        return "";
    }

    private String toQueryParam(String kw) {
        return defaultParam.entrySet().stream()
                .map(e -> String.join("=", e.getKey(), e.getValue()))
                .collect(Collectors.joining("&")) + "&" + "search=" + kw;
    }
}
