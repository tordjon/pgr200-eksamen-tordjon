package no.kristiania.pgr200.program.command.connecting;

import static no.kristiania.pgr200.program.ArgumentParser.getArgument;

import no.kristiania.pgr200.database.dao.TalkDao;
import no.kristiania.pgr200.database.dao.TimeslotDao;
import no.kristiania.pgr200.program.command.Command;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

public class ConnectTalkWithTimeslotCommand extends Command {

    private UUID talkId;
    private UUID timeslotId;

    private ConnectTalkWithTimeslotCommand withTalkId(UUID talkId) {
        this.talkId = talkId;
        return this;
    }

    private ConnectTalkWithTimeslotCommand withTimeslotId(UUID timeslotId) {
        this.timeslotId = timeslotId;
        return this;
    }

    @Override
    public Command build(String[] strings) throws IllegalArgumentException {
        UUID talkId = UUID.fromString(getArgument("-talk", strings, null));
        UUID timeslotId = UUID.fromString(getArgument("-timeslot", strings, null));

        return new ConnectTalkWithTimeslotCommand()
                .withTalkId(talkId)
                .withTimeslotId(timeslotId);
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {
        TimeslotDao dao = new TimeslotDao(dataSource);
        dao.connectTalkToTimeslot(talkId, timeslotId);
    }

}
