package com.vision.movie;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vision.crawler.db.MovieDataBaseHandler;
import com.vision.crawler.db.model.Movie;
import com.vision.crawler.db.model.MovieLink;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private MovieDataBaseHandler handler = new MovieDataBaseHandler();
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private int max = 0;
	private int min = 0;
	private static final int PER_PAGE_MOVIES = 9;
	long pages = 0;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(Locale locale, Model model) {
		max = handler.getMaxMovieId();
		min = handler.getMinMovieId();
		logger.info("Getting first page movies");

		ModelAndView mav = new ModelAndView("home");
		pages = (max - min) / PER_PAGE_MOVIES;
		mav.addObject("pages", pages);
		List<Movie> movies = handler.getMoviesById((max - PER_PAGE_MOVIES + 1), PER_PAGE_MOVIES);
		mav.addObject("movies", movies);
		mav.addObject("start", max - PER_PAGE_MOVIES + 1);
		mav.addObject("currentPage", 1);
		return mav;
	}
	
	@RequestMapping(value = "/Page/{pageNumber}", method = RequestMethod.GET)
	public ModelAndView page(@PathVariable int pageNumber) {
		if( max == 0 ) {
			max = handler.getMaxMovieId();
			min = handler.getMinMovieId();
			pages = (max - min) / PER_PAGE_MOVIES;
		}
		
		logger.info("Getting movies page: "+ pageNumber);

		ModelAndView mav = new ModelAndView("home");
		
		mav.addObject("pages", pages);
		List<Movie> movies = handler.getMoviesById((max - (PER_PAGE_MOVIES * pageNumber) + 1), PER_PAGE_MOVIES);
		mav.addObject("movies", movies);
		mav.addObject("start", (max - (PER_PAGE_MOVIES * pageNumber) + 1));
		mav.addObject("currentPage", pageNumber);
		return mav;
	}
	
	
	@RequestMapping(value = "/play/{mId}", method = RequestMethod.GET)
	public ModelAndView play(@PathVariable int mId) {
		
		List<Movie> movies = handler.getMoviesById(mId, 1);
		if(movies == null || movies.size() == 0 || movies.get(0).getMovieLinks().size() == 0) {
			ModelAndView mav = new ModelAndView("Error");
			mav.addObject("message", "Sorry.. This video is currupted..");
			return mav;
		}
		Movie movie = movies.get(0);
		logger.info("Playing movie: "+ movie.getName());
		ModelAndView mav = new ModelAndView("playMovie");
		MovieLink link = null;
		for(MovieLink movieLink: movie.getMovieLinks()) {
			link = movieLink;
			if(movieLink.getLink().contains("openload"))
			{
				break;
			}
		}
		mav.addObject("movie", movie);
		mav.addObject("movieLink", link.getLink());
		return mav;
	}

}
