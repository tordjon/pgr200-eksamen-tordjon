package no.kristiania.pgr200.server.command.connecting;

import no.kristiania.pgr200.server.command.Command;
import no.kristiania.pgr200.server.database.dao.TimeslotDao;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class ConnectTimeslotWithDayCommand extends Command {

    UUID timeslotId;
    UUID dayId;

    public ConnectTimeslotWithDayCommand withTimeslotId(UUID timeslotId) {
        this.timeslotId = timeslotId;
        return this;
    }

    public ConnectTimeslotWithDayCommand withDayId(UUID dayId) {
        this.dayId = dayId;
        return this;
    }

    @Override
    public Command build(HashMap<String, String> parameters) throws IllegalArgumentException {
        UUID timeslotId = getId(parameters.get("timeslot"));
        UUID dayId = getId(parameters.get("day"));

        return new ConnectTimeslotWithDayCommand()
                .withTimeslotId(timeslotId)
                .withDayId(dayId);
    }

    @Override
    public void execute(DataSource dataSource) throws SQLException {

        if (timeslotId == null || dayId == null) {
            System.out.println("both \"-timeslot\" and \"-day\" is required.");
            return;
        }

        TimeslotDao dao = new TimeslotDao(dataSource);
        dao.connectTimeslotToDay(timeslotId, dayId);
    }

}