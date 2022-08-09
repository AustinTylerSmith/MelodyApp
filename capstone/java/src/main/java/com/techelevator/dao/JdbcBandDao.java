package com.techelevator.dao;

import com.techelevator.model.Band;
import com.techelevator.model.BandNotFoundException;
import com.techelevator.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcBandDao implements BandDao {
    private final JdbcTemplate jdbcTemplate;
    private final JdbcUserDao jdbcUserDao;
    public JdbcBandDao(JdbcTemplate jdbcTemplate, JdbcUserDao jdbcUserDao){
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcUserDao = jdbcUserDao;
    }



    @Override
    public List<Band> listAllBands() {
        List<Band> allBands = new ArrayList<>();
        String sql = "SELECT band_id, band_name, description, genre, image_link FROM band";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Band band = mapRowToBand(results);
                allBands.add(band);
            }
            return allBands;

    }

    @Override
    public List<Band> listBandsByGenre(String genreName) {
        List<Band> bandsOfGenre = new ArrayList<>();
        String sql = "SELECT b.band_id, b.band_name, b.description " +
                "FROM band AS b JOIN band_genre AS bg ON b.band_id = bg.band_id " +
                "JOIN genre AS g ON bg.genre_id = g.genre_id " +
                "WHERE g.genre_name = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, genreName);
        while (results.next()) {
            Band band = mapRowToBand(results);
            bandsOfGenre.add(band);
        }
        return bandsOfGenre;
    }

    @Override
    public Band getBandByName(String bandName) throws BandNotFoundException {
        String sql = "SELECT band_id, band_name, description FROM band WHERE band_name = ?;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, bandName);
            Band theBand = new Band();
            if (results.next()) {
                theBand = mapRowToBand(results);
                return theBand;
            }
            throw new BandNotFoundException();
    }

    @Override
    public boolean createBand(String bandName, String description, String imageLink, String genre, Principal principal){
        Band newBand = new Band();
        newBand.setBandName(bandName);
        newBand.setDescription(description);
        String sql = "INSERT INTO band (band_name, description, genre, image_link) VALUES (?, ?, ?, ?) RETURNING band_id;";
        int bandId = jdbcTemplate.queryForObject(sql, int.class, bandName, description, genre, imageLink);
        newBand.setBandID(bandId);
        User user = jdbcUserDao.findByUsername(principal.getName());
        String sql2 = "INSERT INTO band_user (user_id, band_id) VALUES (?,?) RETURNING band_id;";
        int bandId2 = jdbcTemplate.queryForObject(sql2, int.class, user.getId(), newBand.getBandId());
        String newRole = "USER, BAND_MANAGER";
        user.setAuthorities(newRole);
        String sql3 = "UPDATE users SET role = ? WHERE user_id = ?;";
        jdbcTemplate.update(sql3, newRole, user.getId());
        return true;
    }

    private Band mapRowToBand(SqlRowSet rs) {
        Band band = new Band();
        band.setBandID(rs.getInt("band_id"));
        band.setBandName(rs.getString("band_name"));
        band.setDescription(rs.getString("description"));
        band.setGenre(rs.getString("genre"));
        band.setImageLink((rs.getString("image_link")));
        return band;
    }

}
