package se.vgregion.security.sign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.ticket.Ticket;
import se.vgregion.ticket.TicketException;
import se.vgregion.ticket.TicketManager;
import se.vgregion.web.dto.TicketDto;
import se.vgregion.web.security.services.SignatureData;
import se.vgregion.web.security.services.SignatureService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * RESTful web service implementation of {@link AbstractSignController}.
 * <p/>
 * OBS!! Not fully implemented yet!
 *
 * @author Anders Asplund - <a href="http://www.callistaenterprise.se">Callista Enterprise</a>
 * @author Patrik Bergström - <a href="http://www.knowit.se">Know IT</a>
 */
@Path("/")
@Produces("application/json")
public class RestSignController extends AbstractSignController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSignController.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    /**
     * Constructs an instance of RestSignController.
     *
     * @param signatureService a signatureService
     * @param eLegTypes        a repository of e-legitimations
     * @param ticketManager    the {@link TicketManager} to use
     */
    @Autowired
    public RestSignController(SignatureService signatureService, Repository<ELegType, String> eLegTypes,
                              TicketManager ticketManager) {
        super(signatureService, eLegTypes, ticketManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#getClientTypes()
     */
    @Override
    @ResponseBody
    public Collection<ELegType> getClientTypes() {
        return super.getClientTypes();
    }

    /*
     * (non-Javadoc)
     * 
     * @see AbstractSignController#prepareSign(SignatureData)
     */
    @Override
    public String prepareSign(@RequestBody SignatureData signData) throws SignatureException {
        return super.prepareSign(signData);
    }
    /**
     * Method to request a new {@link Ticket}.
     *
     * @param serviceId the requester must have a serviceId in order to be authorized to receive a {@link Ticket}
     * @return the {@link Ticket} as a {@link String} in the HTTP response body
     */
    @GET
    @Path("/solveTicket/{serviceId}")
    public String solveTicket(@PathParam("serviceId") String serviceId) {
        LOGGER.info("Client with serviceId=" + serviceId + " requests a ticket.");
        Ticket ticket;
        try {
            ticket = getTicketManager().solveTicket(serviceId);
        } catch (TicketException e) {
            throw new WebApplicationException(e, Response.Status.FORBIDDEN);
        }
        return new TicketDto(ticket).toString();
    }


}
