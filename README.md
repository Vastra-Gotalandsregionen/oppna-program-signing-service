
<td id="wikicontent" class="psdescription">
  <h2>
    <a name="Bakgrund_och_ändamål">
    </a>
    Bakgrund och ändamål
    <a href="#Bakgrund_och_ändamål" class="section_anchor">
    </a>
  </h2>
  <p>
    En elektronisk signatur ska säkerställa att elektroniskt överförd information inte har ändrats och för att identifiera informationens avsändare. Genom kryptering skyddas uppgifter i ett dokument mot obehörig åtkomst. 
  </p>
  <h3>
    <a name="Vad_är_en_elektronisk_signatur">
    </a>
    Vad är en elektronisk signatur
    <a href="#Vad_är_en_elektronisk_signatur" class="section_anchor">
    </a>
  </h3>
  <p>
    En elektroniskt underskriven handling består av två delar. Dels 
    <i>
      texten
    </i>
     som skall signeras och dels själva signaturen. För att säkerställa att en 
    <i>
      text
    </i>
     är identisk vid två olika tillfällen beräknas en kontrollsumma av 
    <i>
      texten
    </i>
    . Denna kontrollsumma är 
    <i>
      alltid
    </i>
     densamma så länge 
    <i>
      texten
    </i>
     inte har förändrats och därmed kan man garantera att 
    <i>
      texten
    </i>
     inte har förändrats över tid. Detta är dock inte tillräckligt utan man behöver även kunna säkerställa 
    <i>
      vem
    </i>
     som har utfärdat 
    <i>
      texten
    </i>
    . Detta görs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan 
    <i>
      endast
    </i>
     göras med hjälp av undertecknarens publika nyckel. Nu kan man med säkerhet knyta en viss 
    <i>
      text
    </i>
     till en bestämd utställare. 
  </p>
  <h2>
    <a name="Hur_använder_jag_signeringstjänsten?">
    </a>
    Hur använder jag signeringstjänsten?
    <a href="#Hur_använder_jag_signeringstjänsten?" class="section_anchor">
    </a>
  </h2>
  <p>
    Första steget för en applikation är att erbjuda signering för en användare. Efter detta tar signeringstjänsten över och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det innebär, signeringstjänsten tar även hand om certifikatskontrollen som bla. verifierar att certifikatet som används i signaturen inte är spärrat. Det den nyttjande applikationen behöver ta hänsyn till är lagringen av signaturen. Nedan visas en schematisk bild över hur flödet ser ut, de heldragna pilarna är de interaktioner som måste hanteras/implementeras av applikationen. 
    <img src="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/images/Signatureservice-process.png"/>
  </p>
  <p>
    <a href="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/InDepth" rel="nofollow">
      Läs mer
    </a>
  </p>
</td>

<td id="wikicontent" class="psdescription">
  <h2>
    <a name="Bakgrund_och_ändamål">
    </a>
    Bakgrund och ändamål
    <a href="#Bakgrund_och_ändamål" class="section_anchor">
    </a>
  </h2>
  <p>
    En elektronisk signatur ska säkerställa att elektroniskt överförd information inte har ändrats och för att identifiera informationens avsändare. Genom kryptering skyddas uppgifter i ett dokument mot obehörig åtkomst. 
  </p>
  <h3>
    <a name="Vad_är_en_elektronisk_signatur">
    </a>
    Vad är en elektronisk signatur
    <a href="#Vad_är_en_elektronisk_signatur" class="section_anchor">
    </a>
  </h3>
  <p>
    En elektroniskt underskriven handling består av två delar. Dels 
    <i>
      texten
    </i>
     som skall signeras och dels själva signaturen. För att säkerställa att en 
    <i>
      text
    </i>
     är identisk vid två olika tillfällen beräknas en kontrollsumma av 
    <i>
      texten
    </i>
    . Denna kontrollsumma är 
    <i>
      alltid
    </i>
     densamma så länge 
    <i>
      texten
    </i>
     inte har förändrats och därmed kan man garantera att 
    <i>
      texten
    </i>
     inte har förändrats över tid. Detta är dock inte tillräckligt utan man behöver även kunna säkerställa 
    <i>
      vem
    </i>
     som har utfärdat 
    <i>
      texten
    </i>
    . Detta görs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan 
    <i>
      endast
    </i>
     göras med hjälp av undertecknarens publika nyckel. Nu kan man med säkerhet knyta en viss 
    <i>
      text
    </i>
     till en bestämd utställare. 
  </p>
  <h2>
    <a name="Hur_använder_jag_signeringstjänsten?">
    </a>
    Hur använder jag signeringstjänsten?
    <a href="#Hur_använder_jag_signeringstjänsten?" class="section_anchor">
    </a>
  </h2>
  <p>
    Första steget för en applikation är att erbjuda signering för en användare. Efter detta tar signeringstjänsten över och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det innebär, signeringstjänsten tar även hand om certifikatskontrollen som bla. verifierar att certifikatet som används i signaturen inte är spärrat. Det den nyttjande applikationen behöver ta hänsyn till är lagringen av signaturen. Nedan visas en schematisk bild över hur flödet ser ut, de heldragna pilarna är de interaktioner som måste hanteras/implementeras av applikationen. 
    <img src="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/images/Signatureservice-process.png"/>
  </p>
  <p>
    <a href="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/InDepth" rel="nofollow">
      Läs mer
    </a>
  </p>
</td>

<hr/>

  <p>
    <tt>
      oppna-program-signing-service
    </tt>
     Är en del i Västra Götalandsregionens satsning på öppen källkod inom ramen för 
    <a href="http://vastra-gotalandsregionen.github.io/oppna-program/">
      Öppna Program
    </a>
    . 
  </p>
