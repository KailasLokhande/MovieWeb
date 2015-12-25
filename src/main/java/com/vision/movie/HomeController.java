package com.vision.movie;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vision.crawler.db.MovieDataBaseHandler;
import com.vision.crawler.db.model.Movie;

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

}
