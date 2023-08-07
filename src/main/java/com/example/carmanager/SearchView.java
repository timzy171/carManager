package com.example.carmanager;


import com.example.carmanager.entity.Car;
import com.example.carmanager.repo.CarRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route("/")
@CssImport(value = "./styles/searchView.css")
public class SearchView extends VerticalLayout {
    CarRepository carRepository;
    public SearchView(CarRepository carRepository) throws IOException {
        this.carRepository = carRepository;
        List<Car> carList = carRepository.findAll();
        Set<String> marks = new HashSet<>();
        for(Car car : carList){
            marks.add(car.mark);
        }
        TextField searchText = new TextField();
        searchText.setPlaceholder("Input car name");
        Button searchButton = new Button();
        searchButton.setText("FIND");
        add(searchText);
        add(searchButton);
        setClassNames(searchText,searchButton);

        var hl = new HorizontalLayout();
        hl.setSpacing(false);
        hl.getThemeList().add("spacing-xl");
        hl.setClassName("firstHorizontalLayout");
        int i = 0;
        for(String mark : marks){
            Image carImage = new Image("logo/" + mark.toLowerCase() + ".png",mark);
            carImage.setWidth("100px");
            carImage.setHeight("100px");
            carImage.addClickListener(imageClickEvent -> {
                UI.getCurrent().navigate("/search/" + mark.toLowerCase());
            });
            if(i <= 9){
                hl.add(carImage);
                ++i;
            }
            else{
                add(hl);
                hl = new HorizontalLayout();
                hl.setSpacing(false);
                hl.getThemeList().add("spacing-xl");
                hl.add(carImage);
                i = 1;
            }
        }

        searchButton.addClickListener(event -> {
            String inputCar = searchText.getValue().toLowerCase().trim();
            UI.getCurrent().navigate("/search/" + inputCar);
        });
    }

    private void setClassNames(TextField searchText,Button searchButton){
        searchText.setClassName("sTextField");
        searchButton.setClassName("sButton");
    }
}
