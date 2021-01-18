package resources;

import Representation.*;
import Representation.NumberOfUsr;
import Service.DistrictService;
import Service.Location;
import javafx.util.Pair;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiretorioResource {


    @GET @Path("User")
    public NumberOfUsr numberOfUsrOfDistrict(@QueryParam("distrito") String district) {

        Distrito d = Distrito.findDistrict(district);
        if(d == null){
            return new NumberOfUsr(Response.status(400).build().getStatus(),"Bad request",0);
        }

        int numberOfUsrs;

        DistrictService ds = DistrictService.getInstance();

        numberOfUsrs = ds.getNumberOfUsersInDistrict(district);


        return new NumberOfUsr(Response.status(200).build().getStatus(), d.toString(),numberOfUsrs);
    }


    @GET @Path("Infected")
    public NumberOfInfected numberOfInfectedOfDistrict(@QueryParam("distrito") String district) {

        Distrito d = Distrito.findDistrict(district);
        if(d == null){
            return new NumberOfInfected(Response.status(400).build().getStatus(),"Bad request",0);
        }

        int answer;

        DistrictService ds = DistrictService.getInstance();

        answer = ds.getNumberOfInfectedInDistrict(district);


        return new NumberOfInfected(Response.status(200).build().getStatus(), d.toString(),answer);
    }


    @GET @Path("Racio")
    public Top5District Top5DistrictRacio() {
        List<Pair<Float,Distrito>> respostas = new ArrayList<>();

        Distrito[] lista = Distrito.values();
        DistrictService ds = DistrictService.getInstance();

        for(Distrito entry :lista){
            System.out.println("iter");
            Pair<Float,Distrito> par = new Pair<>(ds.getRacioOfDistrict(entry.toString()),entry);
            respostas.add(par);
        }

        respostas.sort(new Comparator<Pair<Float, Distrito>>() {
            @Override
            public int compare(Pair<Float, Distrito> o1, Pair<Float, Distrito> o2) {
                if (o1.getKey() > o2.getKey()) {
                    return -1;
                } else if (o1.getValue().equals(o2.getValue())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        String top1 = respostas.get(0).getValue().toString() + " - " + respostas.get(0).getKey();
        String top2 = respostas.get(1).getValue().toString() + " - " + respostas.get(1).getKey();
        String top3 = respostas.get(2).getValue().toString() + " - " + respostas.get(2).getKey();
        String top4 = respostas.get(3).getValue().toString() + " - " + respostas.get(3).getKey();
        String top5 = respostas.get(4).getValue().toString() + " - " + respostas.get(4).getKey();

        return new Top5District(Response.status(200).build().getStatus(),top1,top2,top3,top4,top5);
    }

    @GET @Path("Locations")
    public Top5District getLocationOfDistrictWithMostPeople() {
        List<Location> respostas = new ArrayList<>();

        Distrito[] lista = Distrito.values();
        DistrictService ds = DistrictService.getInstance();

        for(Distrito entry :lista){
            List<Location> tmp = ds.getLocationOfDistrictWithMostPeople(entry.toString());
            respostas.addAll(tmp);
        }

        respostas.sort(new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                if (o1.getNumPessoas() > o2.getNumPessoas()) {
                    return -1;
                } else if (o1.getNumPessoas() == o2.getNumPessoas()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        List<String> tops = new ArrayList<>();

        for(Location l : respostas){
            tops.add(l.toString());
        }
        while (tops.size()< 5){
            tops.add(" ");
        }

        String top1 = tops.get(0);
        String top2 = tops.get(1);
        String top3 = tops.get(2);
        String top4 = tops.get(3);
        String top5 = tops.get(4);


        return new Top5District(Response.status(200).build().getStatus(),top1,top2,top3,top4,top5);
    }


    @GET @Path("Disease")
    public NumberOfSick numberOfInfectedOfDistrict() {

        float answer;
        List<Float> respostas = new ArrayList<>();

        DistrictService ds = DistrictService.getInstance();

        Distrito[] lista = Distrito.values();
        for(Distrito entry :lista){
            respostas.add(ds.getUsersWhoCrossedWithSickPeople(entry.toString()));
        }
        int sum = 0;
        for(int i = 0; i< respostas.size();i++){
            sum += respostas.get(i);
        }

        float size = respostas.size();
        answer = sum/size;


        return new NumberOfSick(Response.status(200).build().getStatus(), answer);
    }







}

