package se.vgregion.security.sign;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.domain.security.pkiclient.ELegType;
import se.vgregion.domain.security.pkiclient.ELegTypeRepository;
import se.vgregion.domain.web.BrowserType;
import se.vgregion.web.security.services.SignatureService;

@Controller
public class SignController {
    @Autowired
    private SignatureService signatureService;

    @Autowired
    private ELegTypeRepository eLegTypes;

    @ModelAttribute("clientTypes")
    public Collection<ELegType> getClientTypes() {
        return Collections.unmodifiableCollection(eLegTypes.findAll());
    }

    @RequestMapping(value = "/prepare", method = RequestMethod.POST)
    public String prepareSign(@RequestParam("tbs") String tbs,
            @RequestParam(value = "submitUri") String submitUri,
            @RequestParam(value = "clientType", required = false) String clientType, Model model,
            HttpServletRequest request) throws IOException {

        if (clientType == null) {
            model.addAttribute("tbs", tbs);
            model.addAttribute("submitUri", submitUri);
            return "clientTypeSelection";
        }

        String userAgent = request.getHeader("User-Agent");
        model.addAttribute("browserType", BrowserType.fromUserAgent(userAgent));
        String pkiPostBackUrl = buildPkiPostBackUrl(tbs, submitUri, request);
        SignForm signForm = new SignForm(clientType, tbs, pkiPostBackUrl);
        model.addAttribute("signData", signForm);

        ELegType eLegType = eLegTypes.find(clientType);
        return eLegType.getPkiClientName();
    }

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    public String postback(@RequestParam(value = "SignedData", required = false) String signedData,
            @RequestParam(value = "tbs", required = false) String tbs,
            @RequestParam(value = "submitUri", required = false) String submitUri) throws URISyntaxException,
            SignatureException {

        String redirectLocation = signatureService.save(tbs, new URI(submitUri), signedData);
        if (redirectLocation != null) {
            return "redirect:" + redirectLocation;
        }
        return "verified";
    }

    private String buildPkiPostBackUrl(String tbs, String submitUri, HttpServletRequest req) {
        StringBuilder pkiPostUrl = new StringBuilder();
        String verifyUrl = "http" + (req.isSecure() ? "s" : "") + "://" + req.getServerName() + ":"
                + req.getServerPort() + req.getContextPath() + "/sign/verify?submitUri=";
        pkiPostUrl.append(verifyUrl);
        pkiPostUrl.append(submitUri);
        pkiPostUrl.append("&tbs=");
        pkiPostUrl.append(tbs);

        return pkiPostUrl.toString();
    }
}
