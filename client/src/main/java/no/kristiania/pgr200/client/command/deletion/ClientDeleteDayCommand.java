package no.kristiania.pgr200.client.command.deletion;

import no.kristiania.pgr200.core.http.HttpRequest;
import no.kristiania.pgr200.core.http.HttpResponse;
import no.kristiania.pgr200.client.command.ClientCommand;
import no.kristiania.pgr200.core.command.deletion.DeleteDayCommand;
import no.kristiania.pgr200.core.http.uri.Uri;
import no.kristiania.pgr200.core.model.Day;

import javax.sql.DataSource;
import java.io.IOException;

public class ClientDeleteDayCommand extends DeleteDayCommand implements ClientCommand {


    @Override
    public HttpResponse execute(DataSource dataSource) throws IOException {
        parameters.put("id", id.toString());

        Uri uri = new Uri("/api/delete/day", parameters);
        HttpRequest req = new HttpRequest("localhost", 8080, uri.toString());

        HttpResponse response = req.execute();
        if (checkForError(response)) {
            return response;
        }

        System.out.println("Deleted requested day");
        Day retrieved = gson.fromJson(response.getBody(), Day.class);
        System.out.println(retrieved);
        return response;
    }
}
