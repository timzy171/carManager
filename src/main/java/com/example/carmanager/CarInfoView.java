package com.example.carmanager;
import com.example.carmanager.repo.CarRepository;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;

import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.ui.UI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Route("/search/carInfo")
@CssImport(value = "./styles/carInfo.css")
public class CarInfoView  extends VerticalLayout implements HasUrlParameter<String> {
    @Autowired
    CarRepository carRepository;
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String car) {
        add(HelloPage.getMenu());
        String[] carInfo = car.split("-");
        String mark = carInfo[0];
        String model = "";
        if(carInfo.length > 2){
            for (int i = 1; i < carInfo.length; i++) {
                model += carInfo[i] += "_";
            }
            model = model.substring(0,model.length() - 1);
        }
        else{
            model = carInfo[1];
        }
        String URL;
        String country = carRepository.findByMarkAndModel(mark + model).get(0).country;
        try {
            if(model.contains(" ")){
                model = model.replaceAll(" ","_");
                URL = "http://www.motorpage.ru/" + firstUpperCase(mark) + "/" + model + "/last/";
            }
            else{
                URL = "http://www.motorpage.ru/" + firstUpperCase(mark) + "/" + firstUpperCase(model) + "/last/";

            }
            addInfoToPage(URL,country);
        } catch (IOException e) {
            System.out.println(car);
            throw new RuntimeException(e);
        }
    }

    public static String firstUpperCase(String word){
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }

    public void addInfoToPage(String URL,String country) throws IOException {
        Document document = Jsoup.connect(URL).get();
        addImageToPage(document);
        H1 carName = new H1(document.select("h1").first().text());
        var hl = new HorizontalLayout();
        hl.addClassName("carName");
        hl.add(carName);
        Image flag = new Image("flags/" + country + ".png","FLAG");
        flag.setClassName("flag");
        hl.add(flag);
        add(hl);

        Elements elements = document.getElementsByAttributeValue("itemprop","description");
        var info = elements.get(0).getAllElements();

        for (int i = 0; i < info.size(); i++) {
            if(info.get(i).is("h2")){
                H2 text = new H2(firstUpperCase(info.get(i).text()));
                text.addClassName("title");
                add(text);
            }
            if(info.get(i).is("p")){
                H4 text = new H4(info.get(i).text());
                text.addClassName("info");
                add(text);
            }
        }

        addVideoToPage(document);

    }

    public void addImageToPage(Document document) {
        var hl = new HorizontalLayout();
        Element imgInfo = document.getElementsByAttributeValue("itemprop","image").first();
        String imageURL = imgInfo.absUrl("src");
        Image carImage = new Image(imageURL,"Error");
        carImage.setClassName("carImage");
        hl.add(carImage);
        Elements images = document.select("div.model-img").select("figure");
        for (int i = 0; i < 2; i++) {
            Element imgElem = images.get(i).child(0);
            Image image = new Image(imgElem.absUrl("src"),"error");
            image.setClassName("carImage");
            hl.add(image);
        }
        hl.setClassName("imageLayout");
        add(hl);
    }

    public void addVideoToPage(Document document){
        String videoURL = null;
        IFrame iFrame = new IFrame();
        try {
            videoURL = document.select("iframe").first().absUrl("src");
            iFrame.setSrc(videoURL);
        }
        catch (Exception e){
        }
        iFrame.setAllow("accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture");
        iFrame.getElement().setAttribute("allowfullscreen", true);
        iFrame.getElement().setAttribute("frameborder", "0");
        iFrame.setHeight("500px");
        iFrame.setWidth("100%");
        if(videoURL != null){
            add(iFrame);
        }
    }
}
