package no.kristiania.pgr200.server.command.insertion;

import no.kristiania.pgr200.server.command.Command;
import no.kristiania.pgr200.server.database.dao.Dao;
import no.kristiania.pgr200.server.database.dao.TalkDao;
import no.kristiania.pgr200.server.database.model.Talk;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

public class InsertTalkCommand extends Command {

    private String title;
    private String description;
    private String topic;


    private InsertTalkCommand withTitle(String title) {
        this.title = title;
        return this;
    }

    private InsertTalkCommand withDescription(String description) {
        this.description = description;
        return this;
    }

    private InsertTalkCommand withTopic(String topic) {
        this.topic = topic;
        return this;
    }


    @Override
    public InsertTalkCommand build(HashMap<String, String> parameters) throws IllegalArgumentException {
        String title = parameters.get("title");
        String description = parameters.get("description");
        String topic = parameters.get("topic");


        return new InsertTalkCommand()
                .withTitle(title)
                .withDescription(description)
                .withTopic(topic);
    }


    @Override
    public void execute(DataSource dataSource) throws SQLException {
        Dao<Talk> dao = new TalkDao(dataSource);
        Talk talk = new Talk(title, description, topic);

        dao.insert(talk);
    }
}