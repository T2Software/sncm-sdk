
# Pharmatrack SNCM SDK BETA - Exemplos

Pharmatrack SNCM SDK é um conjunto de funções em formato de um SDK Java que acelera o desenvolvimento de aplicações conectadas ao Sistema Nacional de Controle de Medicamentos da ANVISA.
Este SDK é mantido pela T2 Software

## Funcionalidades

- Geração de Eventos de Ativação no Padrão XML do SNCM
- Geração de Eventos de Expedição no Padrão XML do SNCM
- Geração de Eventos de Recebimento no Padrão XML do SNCM
- Geração de Eventos de Finalização no Padrão XML do SNCM
- Assinatura dos Eventos utilizando certificado Digital ICP Brasil
- Transmissão usando autenticação mutua SSL com certificado Digital ICP Brasil

## Como utilizar

### Gerando Mensagem de Ativação para o SNCM


```java
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
```
### Comunicando a Mensagem de Ativação para o SNCM
```java
        // Start the configuration of the SDK
		SDKConfigurator.configure();
		SDKConfigurator.getInstance().setCertpath("c:\\tmp\\cert");

		// Populate certificate information
		String certificatePath = "<path to certificate>";
		String certificatePassword = "<certificate password>";
		String certificateAlias = "<certificate alias>";
		
		// Sign the XML using the ICP Brasil Certificate
		String signedXML = SignXML.signFile(xml, certificatePath, certificatePassword, certificateAlias);
		
		// Connect to SNCM in one line and get the response
		String response = SncmConnectionFactory.getEvtIn().loadClientCertificate(certificatePath).setCertificatePassword(certificatePassword).submitEvent(signedXML);
		
		// Print the response - Demo Only
		System.out.println(response);
```

[T2]: <https://www.t2software.com.br>

Para ter acesso ao SDK, preencha o formulário abaixo.
https://docs.google.com/forms/d/e/1FAIpQLSfYRd4zleUghf9khCsPMwy0UCn3URkO_kmcOjiIwffUu5lOaQ/viewform
