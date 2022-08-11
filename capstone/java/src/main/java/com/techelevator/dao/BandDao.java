package com.techelevator.dao;

import com.techelevator.model.Band;
import com.techelevator.model.BandNotFoundException;

import java.security.Principal;
import java.util.List;

public interface BandDao {

    List<Band> listAllBands();

    List<Band> listBandsByGenre(String genreName) throws BandNotFoundException;

    Band getBandByName(String bandName);

    Band getBandById(int bandId);

    boolean createBand(String bandName, String description, String imageLink, String genre, Principal principal);
}
