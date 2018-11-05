package no.kristiania.pgr200.client.command;

import no.kristiania.pgr200.client.HttpRequest;
import no.kristiania.pgr200.client.HttpResponse;
import no.kristiania.pgr200.core.command.ResetDBCommand;
import no.kristiania.pgr200.core.http.uri.Uri;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class ClientResetDBCommand extends ResetDBCommand implements ClientCommand{

    @Override
    public HttpResponse execute(DataSource dataSource) throws IOException {
        Uri uri = new Uri("/api(resetdb", parameters);
        HttpRequest req = new HttpRequest("localhost", 8080, uri.toString());

        HttpResponse response = req.execute();
        if(checkForError(response)) {
            return response;
        }

        System.out.println("Database is reset");

        return response;
    }

}
