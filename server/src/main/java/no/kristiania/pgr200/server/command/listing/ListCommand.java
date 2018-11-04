package no.kristiania.pgr200.server.command.listing;

import com.google.gson.Gson;
import no.kristiania.pgr200.server.command.Command;

public abstract class ListCommand extends Command {

    @Override
    public <T> void assignStandardHttp(T content) {

        Gson gson = new Gson();
        String json = gson.toJson(content);

        response.setStatus(200);
        response.getHeaders().put("Content-Type", "application/json");
        response.getHeaders().put("Content-Length", json.length() + "");
        response.setBody(json);
    }
}