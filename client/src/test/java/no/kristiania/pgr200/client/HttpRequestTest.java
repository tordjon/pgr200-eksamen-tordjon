package no.kristiania.pgr200.client;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Talk;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HttpRequestTest {
    @Test
    public void shouldReadStatusCode() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com", 80, "/echo?status=200");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
    @Test
    public void tryReadingTalksFromOurServer() throws IOException {
        HttpRequest request = new HttpRequest("localhost", 8080, "/api/list/talks");
        HttpResponse response = request.execute();

        //assertThat(response.getStatusCode()).isEqualTo(404);
        String body = response.getBody();
        System.out.println(body);
        Gson gson = new Gson();


        // Deserialization
        Type collectionType = new TypeToken<Collection<Talk>>(){}.getType();
        Collection<Talk> talks = gson.fromJson(body, collectionType);
        System.out.println(talks.toString());
        for(Talk t : talks){
            System.out.println(t);
        }
    }


    @Test
    public void shouldReadOtherStatusCodes() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com", 80, "/echo?status=404");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(404);
    }


    @Test
    public void shouldReadResponseHeaders() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com", 80,
                "/echo?status=307&Location=http%3A%2F%2Fwww.google.com");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.getHeader("Location")).isEqualTo("http://www.google.com");
    }

    @Test
    public void shouldReadResponseHeadersServer() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com", 80,
                "/echo?status=307&Location=http%3A%2F%2Fwww.google.com");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.getHeader("Server")).isEqualTo("Google Frontend");
    }

    @Test
    public void shouldReadMultipleHeaders() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com", 80,
                "/echo?status=200&x-some-header=some-value");
        HttpResponse response = request.execute();

        assertThat(response.getHeader("x-some-header")).isEqualTo("some-value");
    }

    @Test
    public void shouldReadResponseBody() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com",
                80, "/echo?body=Hello+world!");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello world!");
    }

    @Test
    public void shouldReadAnotherBody() throws IOException {
        HttpRequest request = new HttpRequest("urlecho.appspot.com",
                80, "/echo?status=200&Content-Type=text%2Fhtml&body=This%20response%20has%20an%20amazing%20body.");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("This response has an amazing body.");
    }
}