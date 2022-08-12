package com.techelevator.dao;

import com.techelevator.model.Band;
import com.techelevator.model.BandNotFoundException;
import com.techelevator.model.GenreDTO;

import java.security.Principal;
import java.util.List;

public interface BandDao {

    List<Band> listAllBands();

    List<Band> listBandsByGenre(String genreName) throws BandNotFoundException;

    Band getBandByName(String bandName);

    Band getBandById(int bandId);

    boolean createBand(String bandName, String description, String imageLink, Integer[] genreIds, Principal principal);

    public void setGenres(List<Integer> genreIds, int bandId );
}
