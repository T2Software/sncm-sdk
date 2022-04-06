package br.com.t2software.tt.sample;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import org.joda.time.DateTime;

import br.com.t2software.tt.crypto.SignXML;
import br.com.t2software.tt.sncm.SncmConnectionFactory;
import br.com.t2software.tt.util.TransformXML;
import br.gov.anvisa.sncm.v1_00.ActivationEvent;
import br.gov.anvisa.sncm.v1_00.Dui;
import br.gov.anvisa.sncm.v1_00.Events;
import br.gov.anvisa.sncm.v1_00.Membro;
import br.gov.anvisa.sncm.v1_00.MsgEvtIn;
import br.gov.anvisa.sncm.v1_00.ObjectFactory;



public class SncmSample {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Hello SNCM SDK Samples");
		
		// Generate the SNCM Activation Message
		String xmlActivation = generateMsgEvtInActiv();
		
		System.out.println(xmlActivation);
		
		// Populate certificate information
		String certificatePath = "<path to certificate>";
		String certificatePassword = "<certificate password>";
		String certificateAlias = "<certificate alias>";
		
		// Sign the XML using the ICP Brasil Certificate
		String signedXML = SignXML.signFile(xmlActivation, certificatePath, certificatePassword, certificateAlias);
		
		// Connect to SNCM in one line and get the response
		String response = SncmConnectionFactory.getEvtIn().loadClientCertificate(certificatePath).setCertificatePassword(certificatePassword).submitEvent(signedXML);
		
		// Print the response - Demo Only
		System.out.println(response);
	}
	
	public static String generateMsgEvtInActiv() {

		// Creating the MsgEvtIn
		MsgEvtIn evtin = new MsgEvtIn();
		evtin.setDocId(getUUID());		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		evtin.setCcTime(TransformXML.convertDate(new Date()));

		evtin.setVer("1.00");
		evtin.setLc("pt-BR");
		evtin.setEnv(2);
				
		Membro declarant = new Membro();
		declarant.setCnpj("53056057000179");

		evtin.setDeclarant(declarant);
		evtin.setMbrAgt("13042274000195");
		evtin.setUsrAgt("T2 Software S.A - V1.0");
		//evtin.setCcTime(null);

		// Creating the events

		//Creating activation
		ActivationEvent activ = new ActivationEvent();
		UUID uuid2 = UUID.randomUUID();
		activ.setEvtNotifId(getUUID());
		activ.setPastTime(TransformXML.convertDate(getDate(-30)));
		activ.setRealTime("true");
		activ.setImport(false);

		// Creating the units

		for (int i = 0; i < 2; i++) {

			Dui d = new ObjectFactory().createDui();
			d.setExp(TransformXML.convertDateOnlyMonth(getDate(+360)));
			d.setGtin("03663502000045");
			d.setLot("LOTE");
			d.setSerl(getUUID());

			activ.getDuiOrCompDui().add(d);
		}

		Events evts = new Events();
		evts.getActivOrShptOrRec().add(activ);		
		evtin.setEvts(evts);



		String xml = TransformXML.getObjectToXml(MsgEvtIn.class, evtin);

		return xml;
	}
	
	private static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").toUpperCase().substring(0, 20);
	}

	private static Date getDate(int days) {

		Date date = new Date(); 
		
		Date daysAgo;

		if (days > 0) {
			daysAgo = new DateTime(date).plusDays(days).toDate();
		} else if (days < 0) {
			days = days * -1;
			daysAgo = new DateTime(date).minusDays(days).toDate();
		} else {
			daysAgo = new Date();
		}
		return daysAgo;
	}


}

