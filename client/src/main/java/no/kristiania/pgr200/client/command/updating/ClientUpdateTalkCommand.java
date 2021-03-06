package no.kristiania.pgr200.client.command.updating;

import no.kristiania.pgr200.client.command.ClientCommand;
import no.kristiania.pgr200.core.command.updating.UpdateTalkCommand;
import no.kristiania.pgr200.core.http.HttpRequest;
import no.kristiania.pgr200.core.http.HttpResponse;
import no.kristiania.pgr200.core.http.uri.Uri;
import no.kristiania.pgr200.core.model.Talk;

import javax.sql.DataSource;
import java.io.IOException;

public class ClientUpdateTalkCommand extends UpdateTalkCommand implements ClientCommand {

    @Override
    public HttpResponse execute(DataSource dataSource) throws IOException {

        parameters.put("title", title);
        parameters.put("description", description);
        parameters.put("topic", topic);
        if(id != null)
            parameters.put("id", id.toString());

        Uri uri = new Uri("/api/update/talk", parameters);
        HttpRequest req = new HttpRequest("localhost", 8080, uri.toString());

        HttpResponse response = req.execute();
        if(checkForError(response)) {
            return response;
        }


        System.out.println("Updated talk: ");
        Talk retrieved = gson.fromJson(response.getBody(), Talk.class);
        System.out.println(retrieved);
        return response;
    }
}
