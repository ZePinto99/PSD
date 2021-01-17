package resources;

import Representation.BadRequest;
import Representation.*;
import Representation.NumberOfUsr;
import Service.DistrictService;
import Service.Location;
import jdk.net.SocketFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

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
        TreeMap<Float,Distrito> respostas = new TreeMap<>();

        Distrito[] lista = Distrito.values();
        DistrictService ds = DistrictService.getInstance();

        for(Distrito entry :lista){
            respostas.put(ds.getRacioOfDistrict(entry.toString()),entry);
        }

        String top1 = respostas.get(respostas.lastKey()).toString() + " - " + respostas.lastKey();
        respostas.remove(respostas.lastKey());
        String top2 = respostas.get(respostas.lastKey()).toString() + " - " + respostas.lastKey();
        respostas.remove(respostas.lastKey());
        String top3 = respostas.get(respostas.lastKey()).toString() + " - " + respostas.lastKey();
        respostas.remove(respostas.lastKey());
        String top4 = respostas.get(respostas.lastKey()).toString() + " - " + respostas.lastKey();
        respostas.remove(respostas.lastKey());
        String top5 = respostas.get(respostas.lastKey()).toString() + " - " + respostas.lastKey();

        return new Top5District(Response.status(200).build().getStatus(),top1,top2,top3,top4,top5);
    }

    @GET @Path("Locations")
    public Top5District getLocationOfDistrictWithMostPeople() {
        TreeMap<Integer, Location> respostas = new TreeMap<>();

        Distrito[] lista = Distrito.values();
        DistrictService ds = DistrictService.getInstance();

        for(Distrito entry :lista){
            List<Location> tmp = ds.getLocationOfDistrictWithMostPeople(entry.toString());
            for(Location l : tmp){
                respostas.put(l.getNumInfect(),l);
            }
        }

        String top1 = respostas.get(respostas.lastKey()).toString();
        respostas.remove(respostas.lastKey());
        String top2 = respostas.get(respostas.lastKey()).toString();
        respostas.remove(respostas.lastKey());
        String top3 = respostas.get(respostas.lastKey()).toString();
        respostas.remove(respostas.lastKey());
        String top4 = respostas.get(respostas.lastKey()).toString();
        respostas.remove(respostas.lastKey());
        String top5 = respostas.get(respostas.lastKey()).toString();


        return new Top5District(Response.status(200).build().getStatus(),top1,top2,top3,top4,top5);
    }


    @GET @Path("Disease")
    public NumberOfSick numberOfInfectedOfDistrict() {

        int answer;
        List<Integer> respostas = new ArrayList<>();

        DistrictService ds = DistrictService.getInstance();

        Distrito[] lista = Distrito.values();
        for(Distrito entry :lista){
            respostas.add(ds.getUsersWhoCrossedWithSickPeople(entry.toString()));
        }
        int sum = 0;
        for(int i = 0; i< respostas.size();i++){
            sum += respostas.get(i);
        }
        answer = sum/respostas.size();


        return new NumberOfSick(Response.status(200).build().getStatus(), answer);
    }







}

