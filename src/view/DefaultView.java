package view;

import entity.Movie;
import entity.UserRating;
import interface_adapters.get_watchlist.GetWatchlistController;
import interface_adapters.get_watchlist.GetWatchlistViewModel;
import interface_adapters.movie_info.MovieInfoController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultView extends JPanel {
    private GetWatchlistController getWatchlistController;
    private MovieInfoController movieInfoController;
    private GetWatchlistViewModel getWatchlistViewModel;
    private final Dimension DIMENSIONS = new Dimension(350,275);
    private java.util.List<Movie> movieList;
    private List<UserRating> ratings;
    private JScrollPane scrollPane;
    private JPanel panelList;
    private String user;

    public void createWatchlistPanel() {;
        setLayout(new BorderLayout());

        // Create a scroll pane to hold the panel list
        movieList = getWatchlistViewModel.getState().getMovieList();
        ratings = getWatchlistViewModel.getState().getRatings();
        scrollPane = new JScrollPane(createPanelList(movieList, ratings));
        scrollPane.setPreferredSize(DIMENSIONS);

        add(scrollPane, BorderLayout.CENTER);
    }

    public JPanel createPanelList(List<Movie> movieList, List<UserRating> ratings) {
        panelList = new JPanel(); // Initialize the panelList field
        panelList.setLayout(new BoxLayout(panelList, BoxLayout.Y_AXIS));

        this.ratings = ratings;

        // Add some sample data
        if (movieList != null) {
            for (Movie movie : movieList) {
                panelList.add(createClickablePanel(movie));
            }
        }

        return panelList;
    }


    public JPanel createClickablePanel(Movie movie) {
        JPanel panel = new JPanel();
        //panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setLayout(new BorderLayout(20,0));

        //placeholder url to test image path
        String path = movie.getPosterURL();
        URL url;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            BufferedImage image = ImageIO.read(url);
            if (image != null) {

                Image scaledimage = image.getScaledInstance(60, 90,
                        Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(scaledimage);
                JLabel imagelabel = new JLabel(imageIcon);

                panel.add(imagelabel, BorderLayout.LINE_START);

            } else {
                // Handle the case where the image couldn't be loaded
                JLabel errorLabel = new JLabel("Error loading image");
                panel.add(errorLabel, BorderLayout.LINE_START);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JLabel label = new JLabel(movie.getTitle());
        panel.add(label, BorderLayout.CENTER);

        // Create the dropdown (combobox) for ratings
        ArrayList<String> OPTIONS = new ArrayList<>();
        OPTIONS.add("-");
        OPTIONS.add("1");
        OPTIONS.add("2");
        OPTIONS.add("3");
        OPTIONS.add("4");
        OPTIONS.add("5");
        String[] ratingOptions = {"-","1", "2", "3", "4", "5"};
        JComboBox<String> ratingsDropdown = new JComboBox<>(ratingOptions);
        for (UserRating rating: ratings) {
            if (rating.getMovieId().equals(movie.getImdbID())) {
                int index = OPTIONS.indexOf(String.valueOf(rating.getRating()));
                ratingsDropdown.setSelectedIndex(index);
            }
        }

        JPanel controlsubpanel = new JPanel();
        controlsubpanel.setLayout(new BorderLayout(20,0));
        JButton add = new JButton("+");
        add.setPreferredSize(new Dimension(40,40));
        controlsubpanel.setBorder(new EmptyBorder(30, 0, 30, 0));
        add.setPreferredSize(new Dimension(50,50));
        add.setFont(new Font("Arial", Font.PLAIN, 20)); // adjust font size

        controlsubpanel.add(add, BorderLayout.CENTER);
        controlsubpanel.add(ratingsDropdown, BorderLayout.LINE_END);
        panel.add(controlsubpanel, BorderLayout.LINE_END);

        // Add a click listener to the panel
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //PLACEHOLDER - to be replaced with showing the information pane of the movie
                movieInfoController.execute(movie.getImdbID());
            }
        });

        // Defines normal and hover colors
        Color normalColor = panel.getBackground();
        Color hoverColor = new Color(220, 220, 220);

        // Add MouseListener for hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(normalColor);
            }
        });

        return panel;
    }

}