package br.com.t2software.tt.sample;

import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.bind.JAXBElement;

import java.util.Date;

import org.joda.time.DateTime;

import br.com.t2software.tt.util.TransformXML;
import br.gov.anvisa.sncm.v1_00.Activation;
import br.gov.anvisa.sncm.v1_00.Dui;
import br.gov.anvisa.sncm.v1_00.Events;
import br.gov.anvisa.sncm.v1_00.MsgEvtIn;
import br.gov.anvisa.sncm.v1_00.ObjectFactory;
import br.gov.anvisa.sncm.v1_00.StakeholderId;

public class SncmSample {

	public static void main(String[] args) {
		
		System.out.println("Hello SNCM SDK Samples");

		String xmlActivation = generateMsgEvtInActiv();
		System.out.println(xmlActivation);

	}
	
	
	public static String generateMsgEvtInActiv() {

		// Creating the MsgEvtIn
		MsgEvtIn evtin = new ObjectFactory().createMsgEvtIn();

		UUID uuid = UUID.randomUUID();

		evtin.setDocId(uuid.toString().replace("-", "").toUpperCase().substring(0, 20));

		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		evtin.setCcTime(TransformXML.convertDate(new Date()));

		evtin.setVer("1.00");
		evtin.setLc("pt-BR");
		evtin.setEnv(2);

		StakeholderId declarant = new ObjectFactory().createStakeholderId();
		declarant.setCnpj("53056057000179");

		evtin.setDeclarant(declarant);
		evtin.setMbrAgt("13042274000195");
		evtin.setUsrAgt("T2 Software S.A - V1.0");

		// Creating the events

		Activation activ = new ObjectFactory().createActivation();
		UUID uuid2 = UUID.randomUUID();
		activ.setEvtNotifId(uuid2.toString().replace("-", "").toUpperCase().substring(0, 20));
		activ.setPastTime(TransformXML.convertDate(getDate(-30)));
		activ.setImport(false);

		// Creating the units

		for (int i = 0; i < 2; i++) {

			Dui d = new ObjectFactory().createDui();
			//d.setExp(TransformXML.convertDateExp(new Date()));
			d.setExp(TransformXML.convertDateOnlyMonth(getDate(+360)));
			d.setGtin("03663502000045");
			d.setLot("LOTE");
			// d.setSerl("0101010100101"+i);
			d.setSerl(getUID());

			activ.getDuiOrCompDui().add(d);
		}

		JAXBElement<Activation> jbeact = new ObjectFactory().createEventsActiv(activ);

		Events evts = new ObjectFactory().createEvents();

		evts.getActivOrShptOrRec().add(jbeact);

		evtin.setEvts(evts);

		String xml = TransformXML.getObjectToXml(MsgEvtIn.class, evtin);

		return xml;
	}
	
	private static String getUID() {
		UUID uuid = UUID.randomUUID();

		String ret = uuid.toString().replace("-", "").substring(0, 20).toUpperCase();
		return ret;
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

