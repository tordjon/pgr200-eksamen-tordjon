package no.kristiania.pgr200.server;


import com.google.gson.Gson;
import javafx.beans.binding.BooleanExpression;
import no.kristiania.pgr200.core.http.HttpRequest;
import no.kristiania.pgr200.core.http.HttpResponse;
import no.kristiania.pgr200.core.http.uri.Uri;
import no.kristiania.pgr200.core.model.Conference;
import no.kristiania.pgr200.core.model.Day;
import no.kristiania.pgr200.core.model.Talk;
import no.kristiania.pgr200.core.model.Timeslot;
import no.kristiania.pgr200.server.database.Util;
import no.kristiania.pgr200.server.database.dao.ConferenceDao;
import no.kristiania.pgr200.server.database.dao.DayDao;
import no.kristiania.pgr200.server.database.dao.TalkDao;
import no.kristiania.pgr200.server.database.dao.TimeslotDao;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.Super;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;

public class ServerTest {

    private static DataSource dataSource;
    Gson gson = new Gson();

    static ConferenceDao conferenceDao = new ConferenceDao(dataSource);
    static TalkDao talkDao;
    static TimeslotDao timeslotDao;
    static DayDao dayDao;

    int port = 8080;
    String hostname = "localhost";


    public static void getDataSource() throws IOException {
        dataSource = Util.createDataSource("./../test.properties");
    }

    @BeforeClass
    public static void init() throws IOException {
        getDataSource();
        HttpServer httpServer = new HttpServer( 8080, "./../test.properties");
        httpServer.start();

        talkDao = new TalkDao(dataSource);
        conferenceDao = new ConferenceDao(dataSource);
        timeslotDao = new TimeslotDao(dataSource);
        dayDao = new DayDao(dataSource);


    }
    @Before
    public void resetDatabase() throws IOException {
        Uri resetdburi = new Uri("/api/resetdb");
        HttpRequest resetDbRequest = new HttpRequest(hostname, port, resetdburi.toString());
        resetDbRequest.execute();

        getDataSource(); //"refresh" the dataSource after cleaning flyway.
    }
    @AfterClass
    public static void teatDown(){

        //kill server?
    }




    @Test
    public void shouldCreateDemoConferenceAndDoProperDatabaseModifications() throws SQLException, IOException {


        HttpRequest createDemoConferenceRequest = new HttpRequest(hostname, port, new Uri("/api/createdemo").toString());
        HttpResponse response = createDemoConferenceRequest.execute();

        assertThat(response.getStatusCode()).isEqualTo(200);

        ConferenceDao conferenceDao = new ConferenceDao(dataSource);
        TimeslotDao timeslotDao = new TimeslotDao(dataSource);


        assertThat(conferenceDao.retrieveAll().size()).isEqualTo(1);
        assertThat(dayDao.retrieveAll().size()).isEqualTo(2);
        assertThat(timeslotDao.retrieveAll().size()).isEqualTo(4);
        assertThat(talkDao.retrieveAll().size()).isEqualTo(4);
    }

    @Test
    public void shouldShowSchedule() throws IOException, SQLException {

        HttpRequest createDemoConferenceRequest = new HttpRequest(hostname, port, new Uri("/api/createdemo").toString());
        HttpResponse createDemoConferenceResponse = createDemoConferenceRequest.execute();

        Conference conference = conferenceDao.retrieveAll().get(0);
        //String conferencejson = gson.toJson(conference); ?


        HttpRequest showScheduleRequest = new HttpRequest(hostname, port,
                new Uri("/api/showschedule?id=" + conference.getId().toString()).toString());
        HttpResponse showScheduleResponse = showScheduleRequest.execute();


        assertEquals(showScheduleResponse.getBody(), createDemoConferenceResponse.getBody());

    }



   @Test
    public void shouldListTalks() throws IOException, SQLException {
        Talk talk = new Talk("en talk", "med description", "kjedelig topic");
        Talk otherTalk = new Talk("en talk", "med description", "kjedelig topic");
        talkDao.insert(talk);
        talkDao.insert(otherTalk);

        List<Talk> talks = talkDao.retrieveAll();
        String talksJson = gson.toJson(talks);
        HttpRequest request = new HttpRequest(hostname, port,
                new Uri("/api/list/talks").toString());
        HttpResponse response = request.execute();

        assertEquals(talksJson, response.getBody());
        assertEquals(200,response.getStatusCode());

    }


    @Test
    public void shouldListDays() throws IOException, SQLException {

        Day day = new Day(LocalDate.of(2019, 11, 19));
        Day otherDay = new Day(LocalDate.of(2020, 9, 19));
        dayDao.insert(day);
        dayDao.insert(otherDay);


        List<Day> days = dayDao.retrieveAll();
        String json = gson.toJson(days);

        HttpRequest request = new HttpRequest(hostname, port,
                new Uri("/api/list/days").toString());
        HttpResponse response = request.execute();

        assertEquals(json, response.getBody());
        assertEquals(200,response.getStatusCode());

    }



    @Test
    public void shouldListConferences() throws IOException, SQLException {
        Conference conference = new Conference("Blizzcon");
        Conference otherConference = new Conference("TwitchCOn");
        conferenceDao.insert(conference);
        conferenceDao.insert(otherConference);

        List<Conference> conferences = conferenceDao.retrieveAll();

        String json = gson.toJson(conferences);

        HttpRequest request = new HttpRequest(hostname, port,
                new Uri("/api/list/conferences").toString());
        HttpResponse response = request.execute();

        assertEquals(json, response.getBody());
        assertEquals(200,response.getStatusCode());

    }

    @Test
    public void shouldListTimeslots() throws IOException, SQLException {
        Timeslot timeslot = new Timeslot(LocalTime.of(10,30,0), LocalTime.of(12,0,0));
        Timeslot otherTimeslot = new Timeslot(LocalTime.of(13,0,0), LocalTime.of(14,30,0));
        timeslotDao.insert(timeslot);
        timeslotDao.insert(otherTimeslot);
        List<Timeslot> timeslots = timeslotDao.retrieveAll();
        String json = gson.toJson(timeslots);

        HttpRequest request = new HttpRequest(hostname, port,
                new Uri("/api/list/timeslots").toString());
        HttpResponse response = request.execute();

        assertEquals(json, response.getBody());
        assertEquals(200,response.getStatusCode());

    }

/*
    @Test
    public void shouldConnectDayToConference() throws IOException, SQLException {
        main(new String[]{"help"});
        ConferenceDao conferenceDao = new ConferenceDao(dataSource);
        DayDao dayDao = new DayDao(dataSource);

        Conference conference = new Conference("Blizzcon");
        Day day = new Day(LocalDate.of(2019, 9, 19));
        Day day2 = new Day(LocalDate.of(2019, 9, 20));
        Day day3 = new Day(LocalDate.of(2019, 9, 24));

        conferenceDao.insert(conference);
        dayDao.insert(day);
        dayDao.insert(day2);
        dayDao.insert(day3);

        String[] connectArgs = {"connect", "day-with-conference",
                                "-day", day.getId().toString(),
                                "-conference", conference.getId().toString()};

        main(connectArgs);
        List<Day> connectedDays = dayDao.retrieveByConference(conference.getId());

        assertThat(connectedDays).asList().contains(day);
        assertThat(connectedDays).asList().doesNotContain(day2);
        assertThat(connectedDays).asList().doesNotContain(day3);
    }


    @Test
    public void shouldConnectTimeslotToDay() throws IOException, SQLException {
        main(new String[]{"help"});
        TimeslotDao timeslotDao = new TimeslotDao(dataSource);
        DayDao dayDao = new DayDao(dataSource);

        Timeslot timeslot = new Timeslot(LocalTime.of(10,30), LocalTime.of(12,15));
        Timeslot timeslot2 = new Timeslot(LocalTime.of(12,30), LocalTime.of(13,15));
        Day day = new Day(LocalDate.of(2019, 9, 19));


        timeslotDao.insert(timeslot);
        timeslotDao.insert(timeslot2);
        dayDao.insert(day);


        timeslot = timeslotDao.retrieve(timeslot.getId());
        day = dayDao.retrieve(day.getId());


        String[] connectArgs = {"connect", "timeslot-with-day",
                "-timeslot", timeslot.getId().toString(),
                "-day", day.getId().toString(),
        };

        main(connectArgs);
        List<Timeslot> connectedTimeslots = timeslotDao.retrieveByDay(day.getId());

        assertThat(connectedTimeslots).asList().contains(timeslot);
        assertThat(connectedTimeslots).asList().doesNotContain(timeslot2);
    }


    @Test
    public void shouldConnectTalkToTimeslot() throws IOException, SQLException {
        TimeslotDao timeslotDao = new TimeslotDao(dataSource);
        TalkDao talkDao = new TalkDao(dataSource);

        Timeslot timeslot = new Timeslot(LocalTime.of(8,40), LocalTime.of(9,55));
        Talk talk = new Talk("Testtalk", "talkdesc", "...");
        talkDao.insert(talk);
        timeslotDao.insert(timeslot);

        String[] connectArgs = {"connect", "talk-with-timeslot",
                "-talk", talk.getId().toString(),
                "-timeslot", timeslot.getId().toString()};

        main(connectArgs);
        List<Talk> connectedTalks = talkDao.retrieveByTimeslot(timeslot.getId());

        assertThat(connectedTalks).asList().contains(talk);

    }
    @Test
    public void shouldInsertTalk() throws IOException, SQLException {
        Talk inserted = new Talk("inserted title", "inserted description", "inserted topic");
        String[] args = {
                "insert", "talk",
                "-title", inserted.getTitle(),
                "-description", inserted.getDescription(),
                "-topic", inserted.getTopicTitle()
        };

        Program.setPropertiesFilename("test.properties");

        main(new String[]{"reset", "db"});
        main(args);

        TalkDao talkDao = new TalkDao(dataSource);
        Talk talk = talkDao.retrieveAll().get(0);

        assertThat(talk)
                .isEqualToComparingOnlyGivenFields(inserted,
                        "title", "description", "topicTitle");

    }

    @Test
    public void shouldThrowIllegalArgumentException() {

        String[] args = new String[]{
                "insert", "talk", "-id", "-title"
        };

        main(args);

        String expectedOutput = "option (\"-option\") cannot be followed by another option";
        String actualOutput = outContent.toString().replaceAll("(\\r|\\n)", "");
        assertThat(actualOutput)
                .isEqualTo(expectedOutput);
    }

    @Test
    public void invalidSourceShouldPrintMessage() throws IOException {


        String[] args = {
                "insert", "talk",
                "-title", "talkname",
                "-description", "talkdescription",
                "-topic", "topictittel"
        };
        Program.setPropertiesFilename("WRONG.properties");

        main(args);

        String expectedOutput = "Something went wrong when configuring datasource";
        String actualOutput = outContent.toString().replaceAll("(\\r|\\n)", "");
        assertThat(actualOutput)
                .isEqualTo(expectedOutput);
    }


    @Test
    public void testResetingDatabase() throws  SQLException {
        TalkDao talkDao = new TalkDao(dataSource);

        String[] args = {
                "insert", "talk",
                "-title", "talkname",
                "-description", "talkdescription",
                "-topic", "topictittel"
        };
        Talk talk = new Talk("talkname", "talkdescription", "topictittel");
        main(args);
        List<Talk> talks = talkDao.retrieveAll();
        assertThat(talks).asList().contains(talk);

        String[] args2 = {"reset", "db"};
        main(args2);

        main(new String[]{"help"}); // running main will create tables again
        List<Talk> retrievedEmpty = talkDao.retrieveAll();
        assertThat(retrievedEmpty)
                .asList()
                .isEmpty();
    }



    @Test
    public void TestTalkCommands() throws  SQLException {

        TalkDao talkDao = new TalkDao(dataSource);

        Talk talk = new Talk("Delete this talk", "delete", "..");
        String[] insertArgs = {
                "insert", "talk",
                "-title", talk.getTitle(),
                "-description", talk.getDescription(),
                "-topic", talk.getTopicTitle()
        };

        main(insertArgs);
        Talk retrievedTalk = talkDao.retrieveAll().get(0);
        assertThat(retrievedTalk).isEqualToComparingOnlyGivenFields(talk, "title", "topicTitle", "description");
        talk = retrievedTalk;



        Talk updatedTalk = new Talk(talk.getId(), talk.getTitle(), talk.getDescription(), "updated description");
        String[] updateArgs = {
                "update", "talk",
                "-id", updatedTalk.getId().toString(),
                "-topic", updatedTalk.getTopicTitle()
        };

        main(updateArgs);
        retrievedTalk = talkDao.retrieve(updatedTalk.getId());

        assertThat(retrievedTalk).isEqualToComparingFieldByField(updatedTalk);
        assertThat(retrievedTalk.toString()).isNotEqualTo(talk.toString());


        String[] deleteArgs = {
                "delete", "talk",
                "-id", updatedTalk.getId().toString(),
        };
        main(deleteArgs);
        retrievedTalk = talkDao.retrieve(updatedTalk.getId());
        assertThat(retrievedTalk).isNull();

    }


    @Test
    public void TestTimeslotCommands() throws  SQLException {

        TimeslotDao timeslotDao = new TimeslotDao(dataSource);

        Timeslot timeslot = new Timeslot(LocalTime.of(10, 30, 0), LocalTime.of(12,0,0));
        String[] insertArgs = {
                "insert", "timeslot",
                "-start", timeslot.getStart().toString(),
                "-end", timeslot.getEnd().toString()
        };

        main(insertArgs);
        Timeslot retrievedTimeslot = timeslotDao.retrieveAll().get(0);
        assertThat(retrievedTimeslot).isEqualToComparingOnlyGivenFields(timeslot, "start", "end");
        timeslot = retrievedTimeslot;



        Timeslot updatedTimeslot = new Timeslot(timeslot.getId(), timeslot.getStart(), LocalTime.of(12,30,0));
        String x = updatedTimeslot.getStart().toString();

        String[] updateArgs = {
                "update", "timeslot",
                "-id", updatedTimeslot.getId().toString(),
                "-end", updatedTimeslot.getEnd().toString()
        };

        main(updateArgs);
        retrievedTimeslot = timeslotDao.retrieve(updatedTimeslot.getId());

        assertThat(retrievedTimeslot).isEqualToComparingFieldByField(updatedTimeslot);
        assertThat(retrievedTimeslot.toString()).isNotEqualTo(timeslot.toString());


        String[] deleteArgs = {
                "delete", "timeslot",
                "-id", updatedTimeslot.getId().toString(),
        };
        main(deleteArgs);
        retrievedTimeslot = timeslotDao.retrieve(updatedTimeslot.getId());
        assertThat(retrievedTimeslot).isNull();

    }

    @Test
    public void TestDayCommands() throws  SQLException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        DayDao dayDao = new DayDao(dataSource);

        Day day = new Day(LocalDate.of(2019, 6, 13));
        String[] insertArgs = {
                "insert", "day",
                "-date", day.getDate().format(formatter)
        };

        main(insertArgs);
        Day retrievedDay = dayDao.retrieveAll().get(0);
        assertThat(retrievedDay).isEqualToComparingOnlyGivenFields(day, "date");
        day = retrievedDay;

        Day updatedDay = new Day(day.getId(), LocalDate.of(2019, 6,14));

        String[] updateArgs = {
                "update", "day",
                "-id", updatedDay.getId().toString(),
                "-date", updatedDay.getDate().format(formatter)
        };

        main(updateArgs);
        retrievedDay = dayDao.retrieve(updatedDay.getId());

        assertThat(retrievedDay).isEqualToComparingFieldByField(updatedDay);
        assertThat(retrievedDay.toString()).isNotEqualTo(day.toString());


        String[] deleteArgs = {
                "delete", "day",
                "-id", updatedDay.getId().toString(),
        };
        main(deleteArgs);
        retrievedDay = dayDao.retrieve(updatedDay.getId());
        assertThat(retrievedDay).isNull();

    }

    @Test
    public void TestConferenceCommands() throws  SQLException {

        ConferenceDao conferenceDao = new ConferenceDao(dataSource);

        Conference conference = new Conference("Twitchcon");
        String[] insertArgs = {
                "insert", "conference",
                "-name", conference.getName()
        };

        main(insertArgs);
        Conference retrievedConference = conferenceDao.retrieveAll().get(0);
        assertThat(retrievedConference).isEqualToComparingOnlyGivenFields(conference, "name");
        conference = retrievedConference;

        Conference updatedConference = new Conference(conference.getId(), "TwitchCon 2019");

        String[] updateArgs = {
                "update", "conference",
                "-id", updatedConference.getId().toString(),
                "-name", updatedConference.getName()
        };

        main(updateArgs);
        retrievedConference = conferenceDao.retrieve(updatedConference.getId());

        assertThat(retrievedConference).isEqualToComparingFieldByField(updatedConference);
        assertThat(retrievedConference.toString()).isNotEqualTo(conference.toString());


        String[] deleteArgs = {
                "delete", "conference",
                "-id", updatedConference.getId().toString(),
        };
        main(deleteArgs);
        retrievedConference = conferenceDao.retrieve(updatedConference.getId());
        assertThat(retrievedConference).isNull();

    }
    */
}