package resources;

import Representation.BadRequest;
import Representation.NumberOfUsr;
import jdk.net.SocketFlow;

import java.util.Optional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiretorioResource {


    @GET @Path("user")
    public NumberOfUsr numberOfUsrOfDistrict(@QueryParam("distrito") String district) {
            Distrito d = Distrito.findDistrict(district);

            if(d == null){
                return new NumberOfUsr(Response.status(400).build().getStatus(),"Bad request",0);
            }

            // falta iniciar um serviço que faça os pedidos aos servidores distritais
            int numberOfUsrs = 10;

        return new NumberOfUsr(Response.status(200).build().getStatus(), d.toString(),numberOfUsrs);
    }




}

