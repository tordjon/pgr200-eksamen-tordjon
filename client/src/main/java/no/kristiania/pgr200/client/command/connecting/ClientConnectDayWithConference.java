package no.kristiania.pgr200.client.command.connecting;

import no.kristiania.pgr200.client.command.ClientCommand;
import no.kristiania.pgr200.core.command.connecting.ConnectDayWithConference;
import no.kristiania.pgr200.core.http.HttpRequest;
import no.kristiania.pgr200.core.http.HttpResponse;
import no.kristiania.pgr200.core.http.uri.Uri;

import javax.sql.DataSource;
import java.io.IOException;

public class ClientConnectDayWithConference extends ConnectDayWithConference implements ClientCommand {

    @Override
    public HttpResponse execute(DataSource dataSource) throws IOException {
        parameters.put("day", dayId.toString());
        parameters.put("conference", conferenceId.toString());

        Uri uri = new Uri("/api/connect/day-with-conference", parameters);
        HttpRequest req = new HttpRequest("localhost", 8080, uri.toString());

        HttpResponse response = req.execute();
        if (checkForError(response)) {
            return response;
        }

        System.out.println("Connected specified day with conference");


        return response;
    }
}
