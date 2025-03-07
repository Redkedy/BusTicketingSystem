package com.example.satisotomasyonu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbManager {
    private final String location = start.class.getResource("db.db").toExternalForm();
    private String valData;
    private Connection connection;
    private int role_id;
    private long cellPhone;
    private String role_title;
    private String name, surname;
    private HashMap<Integer, Bus> arrOfBuses = new HashMap<Integer, Bus>();
    private HashMap<Integer, Driver> arrOfDrivers = new HashMap<Integer, Driver>();

    public DbManager() {
        checkDrivers();
    }


    private Connection connect() {
        String dbPrefix = "jdbc:sqlite:";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbPrefix + location);
        } catch (SQLException exception) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                    LocalDateTime.now() + ": SQLite'a şu saatte bağlanılamadı " +
                            location);
            return null;
        }
        return connection;
    }

    protected boolean setPassword(User user, String newPass) throws SQLException {

        connection = connect();
        PreparedStatement statement = connection.prepareStatement("UPDATE Login SET password =? where (userName =? AND password =?)");
        statement.setString(1, newPass);
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.executeUpdate();
        statement.close();
        connection.close();
        user.setPassword(newPass);
        return true;

    }

    protected ObservableList<TicketType> getPersonTypes() throws SQLException {
        ObservableList<TicketType> types = FXCollections.observableArrayList();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT * from TicketTypes");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {

            types.add(new TicketType(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }

        statement.close();
        connection.close();
        return types;


    }

    protected void setPhone(User user, long number) throws SQLException {

        connection = connect();
        PreparedStatement statement = connection.prepareStatement("UPDATE Login SET cellPhone =? where (userName =? AND password =?)");
        statement.setLong(1, number);
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.executeUpdate();
        statement.close();
        connection.close();
        user.setCellPhone(number);


    }

    protected void setValData(User user, String hash) throws SQLException {

        connection = connect();
        PreparedStatement statement = connection.prepareStatement("UPDATE Login SET validation =? where (userName =? AND password =?)");
        statement.setString(1, hash);
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getPassword());
        statement.executeUpdate();
        statement.close();
        connection.close();
        user.setValData(hash);
        valData = hash;


    }

    protected boolean Login(String username, String password) throws SQLException, DisabledUser {
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("Select validation,name,surname,cellPhone from Login Where (userName =? AND password =?)");
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet rs = statement.executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();
            valData = rs.getString("validation");
            this.name = rs.getString("name");
            this.surname = rs.getString("surname");
            this.cellPhone = rs.getLong("cellPhone");
            rs.close();
            connection.close();
            int role_id = getRole(username, password);
            if (role_id == 2) {
                throw new DisabledUser("Kullanıcı devre dışı. Yöneticinize başvurun!");
            }
            return true;
        }
        connection.close();
        return false;
    }

    protected int getRole(String username, String password) throws SQLException {
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("Select role_id from Login Where (userName =? AND password =?)");
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet rs = statement.executeQuery();
        rs.next();
        int role_id = rs.getInt("role_id");
        rs.close();
        connection.close();
        return role_id;
    }

    protected ObservableList<Route> getSpecifiedRoutes(String from, String to, String departureDate, boolean all) throws SQLException {
        getDrivers();
        getBuses();
        ObservableList<Route> arrOfRoutes = FXCollections.observableArrayList();
        connection = connect();
        PreparedStatement statement;
        if (all) {
            statement = connection.prepareStatement("SELECT * from Routes WHERE (fromm=? AND too=? AND departureDate >=?) ORDER BY departureDate");
            statement.setString(1, from);
            statement.setString(2, to);
            statement.setString(3, departureDate + " 00:00");
        } else {
            statement = connection.prepareStatement(
                    "SELECT * from Routes WHERE  (fromm=? AND too=? AND departureDate BETWEEN ? AND ?) ORDER BY departureDate");
            statement.setString(1, from);
            statement.setString(2, to);
            statement.setString(3, departureDate + " 00:00");
            statement.setString(4, departureDate + " 23:59");
        }

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {

            arrOfRoutes.add(new Route(rs.getInt("id"), rs.getInt("price"), rs.getString("departureDate"), rs.getString("arrivalDate"),
                    arrOfBuses.get(rs.getInt("busId")), arrOfDrivers.get(rs.getInt("driver1")),
                    arrOfDrivers.get(rs.getInt("driver2")), arrOfDrivers.get(rs.getInt("driver3")), rs.getString("fromm"), rs.getString("too")));

        }
        rs.close();
        connection.close();
        return arrOfRoutes;

    }

    protected ObservableList<String> getDistinct() throws SQLException {
        ObservableList<String> data = FXCollections.observableArrayList();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT fromm  FROM Routes");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            data.add(rs.getString(1));
        }
        rs.close();
        connection.close();
        return data.sorted();


    }

    protected ObservableList<String> getSeats(long routeId) throws SQLException {
        ObservableList<String> data = FXCollections.observableArrayList();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT t.Seat,c.Gender FROM Ticket t INNER JOIN Customer c on c.CustomerId=t.CustomerId WHERE t.RouteId=?");
        statement.setLong(1, routeId);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            String seat = ((Long) rs.getLong(1)).toString(), gender = ((Integer) rs.getInt(2)).toString();
            data.add(seat + "," + gender);
        }
        rs.close();
        connection.close();
        return data;


    }

    protected int getTicket() {
        String statement = "SELECT f.* FROM Ticket f,(SELECT GroupId,RouteId From Ticket WHERE Id=4) t WHERE CASE WHEN t.GroupId=-1 THEN f.Id=4 ELSE f.GroupId=t.GroupId AND f.RouteId=t.RouteId END";
        return 1;

    }

    protected ObservableList<String> getDistinct(final String from) throws SQLException {
        ObservableList<String> data = FXCollections.observableArrayList();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT too  FROM Routes WHERE fromm=?");
        statement.setString(1, from);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            data.add(rs.getString(1));
        }
        rs.close();
        connection.close();
        return data.sorted();


    }

    protected Long addCustomer(Customer customer) {

        try {
            connection = connect();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Customer (TRId,name,surname,gender) VALUES(?,?,?,?)");
            statement.setLong(1, customer.getTrId());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getSurname());
            statement.setInt(4, customer.getGender());
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        try {
            connection = connect();
            PreparedStatement statement1 = connection.prepareStatement("SELECT CustomerId FROM Customer Where TRId=?");
            statement1.setLong(1, customer.getTrId());
            ResultSet rs = statement1.executeQuery();
            while (rs.next()) {
                customer.setCustomerId(rs.getLong("CustomerId"));
            }

            rs.close();
            connection.close();
        } catch (SQLException ex) {

        }
        return customer.getCustomerId();
    }

    protected Long addTicket(Ticket ticket) {
        try {
            connection = connect();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Ticket (RouteId,CustomerId,CellPhone,Date,Type,Seat,Price,GroupId) VALUES(?,?,?,?,?,?,?,?)");
            statement.setLong(1, ticket.getRouteId().getId());
            statement.setLong(2, ticket.getCustomerId().getCustomerId());
            statement.setLong(3, ticket.getCellPhone());
            statement.setString(4, ticket.getDate());
            statement.setInt(5, ticket.getType());
            statement.setInt(6, ticket.getSeatNo());
            statement.setDouble(7, ticket.getPrice());
            statement.setInt(8, ticket.getGroupId());
            statement.executeUpdate();
            statement.close();
            connection.close();

            connection = connect();
            PreparedStatement statement1 = connection.prepareStatement("SELECT Id FROM Ticket Where RouteId=? AND CustomerId=?");
            statement1.setLong(1, ticket.getRouteId().getId());
            statement1.setLong(2, ticket.getCustomerId().getCustomerId());
            ResultSet rs = statement1.executeQuery();
            while (rs.next()) {
                ticket.setId(rs.getLong("Id"));
            }
            rs.close();
        } catch (SQLException ex) {

        }

        return ticket.getId();
    }

    protected HashMap<Integer, Bus> getBuses() throws SQLException {
        arrOfBuses.clear();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * from Bus");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {

            arrOfBuses.put(rs.getInt("id"), new Bus(rs.getInt("id"), rs.getInt("firstReg"), rs.getInt("numberOfSeats"), rs.getString("make"),
                    rs.getString("model"), rs.getString("plate")));
        }
        rs.close();
        connection.close();
        return arrOfBuses;
    }

    protected HashMap<Integer, Driver> getDrivers() throws SQLException {
        arrOfDrivers.clear();
        connection = connect();
        PreparedStatement statement = connection.prepareStatement("SELECT * from Driver");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            arrOfDrivers.put(rs.getInt("id"), new Driver(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("address"), rs.getString("dateOfBirth"),
                    rs.getInt("gender"), rs.getLong("cellphone"), rs.getLong("TRid")));
        }
        rs.close();
        connection.close();
        return arrOfDrivers;
    }

    protected String getValData() {
        return valData;
    }

    protected ArrayList<Permission> getPermissions() throws ThrowDialog {

        try {
            final int role_id = Role.getRole_Id();
            ArrayList<Permission> perm = new ArrayList<>();
            connection = connect();
            PreparedStatement statement = connection.prepareStatement("Select Permissions,TITLE from Role Where (ID =?)");
            statement.setInt(1, role_id);
            ResultSet rs = statement.executeQuery();
            rs.next();
            String[] permission = rs.getString("Permissions").split(",");
            rs.close();
            for (String i : permission) {

                statement = connection.prepareStatement("Select ID, TITLE from Permissions Where (ID =?)");
                statement.setInt(1, Integer.parseInt(i.trim()));
                rs = statement.executeQuery();
                rs.next();
                perm.add(new Permission(Integer.parseInt(i.trim()), rs.getString("TITLE")));
            }
            connection.close();
            return perm;
        } catch (SQLException ex) {

            throw new ThrowDialog("Veritabanına bağlanırken bir sorun oluştu:" + ex.getMessage());
        }


    }

    private boolean checkDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": SQLite driverları başlatılamadı");
            return false;
        }
    }

    protected String getRole_title() {
        return role_title;
    }

    protected int getRole_id() {
        return role_id;
    }

    protected String getName() {
        return name;
    }

    protected String getSurName() {
        return surname;
    }

    protected long getCellPhone() {
        return this.cellPhone;
    }
}
