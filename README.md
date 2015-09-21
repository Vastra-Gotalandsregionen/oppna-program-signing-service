
<td id="wikicontent" class="psdescription">
  <h2>
    <a name="Bakgrund_och_�ndam�l">
    </a>
    Bakgrund och �ndam�l
    <a href="#Bakgrund_och_�ndam�l" class="section_anchor">
    </a>
  </h2>
  <p>
    En elektronisk signatur ska s�kerst�lla att elektroniskt �verf�rd information inte har �ndrats och f�r att identifiera informationens avs�ndare. Genom kryptering skyddas uppgifter i ett dokument mot obeh�rig �tkomst. 
  </p>
  <h3>
    <a name="Vad_�r_en_elektronisk_signatur">
    </a>
    Vad �r en elektronisk signatur
    <a href="#Vad_�r_en_elektronisk_signatur" class="section_anchor">
    </a>
  </h3>
  <p>
    En elektroniskt underskriven handling best�r av tv� delar. Dels 
    <i>
      texten
    </i>
     som skall signeras och dels sj�lva signaturen. F�r att s�kerst�lla att en 
    <i>
      text
    </i>
     �r identisk vid tv� olika tillf�llen ber�knas en kontrollsumma av 
    <i>
      texten
    </i>
    . Denna kontrollsumma �r 
    <i>
      alltid
    </i>
     densamma s� l�nge 
    <i>
      texten
    </i>
     inte har f�r�ndrats och d�rmed kan man garantera att 
    <i>
      texten
    </i>
     inte har f�r�ndrats �ver tid. Detta �r dock inte tillr�ckligt utan man beh�ver �ven kunna s�kerst�lla 
    <i>
      vem
    </i>
     som har utf�rdat 
    <i>
      texten
    </i>
    . Detta g�rs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan 
    <i>
      endast
    </i>
     g�ras med hj�lp av undertecknarens publika nyckel. Nu kan man med s�kerhet knyta en viss 
    <i>
      text
    </i>
     till en best�md utst�llare. 
  </p>
  <h2>
    <a name="Hur_anv�nder_jag_signeringstj�nsten?">
    </a>
    Hur anv�nder jag signeringstj�nsten?
    <a href="#Hur_anv�nder_jag_signeringstj�nsten?" class="section_anchor">
    </a>
  </h2>
  <p>
    F�rsta steget f�r en applikation �r att erbjuda signering f�r en anv�ndare. Efter detta tar signeringstj�nsten �ver och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det inneb�r, signeringstj�nsten tar �ven hand om certifikatskontrollen som bla. verifierar att certifikatet som anv�nds i signaturen inte �r sp�rrat. Det den nyttjande applikationen beh�ver ta h�nsyn till �r lagringen av signaturen. Nedan visas en schematisk bild �ver hur fl�det ser ut, de heldragna pilarna �r de interaktioner som m�ste hanteras/implementeras av applikationen. 
    <img src="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/images/Signatureservice-process.png"/>
  </p>
  <p>
    <a href="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/InDepth" rel="nofollow">
      L�s mer
    </a>
  </p>
</td>

<td id="wikicontent" class="psdescription">
  <h2>
    <a name="Bakgrund_och_�ndam�l">
    </a>
    Bakgrund och �ndam�l
    <a href="#Bakgrund_och_�ndam�l" class="section_anchor">
    </a>
  </h2>
  <p>
    En elektronisk signatur ska s�kerst�lla att elektroniskt �verf�rd information inte har �ndrats och f�r att identifiera informationens avs�ndare. Genom kryptering skyddas uppgifter i ett dokument mot obeh�rig �tkomst. 
  </p>
  <h3>
    <a name="Vad_�r_en_elektronisk_signatur">
    </a>
    Vad �r en elektronisk signatur
    <a href="#Vad_�r_en_elektronisk_signatur" class="section_anchor">
    </a>
  </h3>
  <p>
    En elektroniskt underskriven handling best�r av tv� delar. Dels 
    <i>
      texten
    </i>
     som skall signeras och dels sj�lva signaturen. F�r att s�kerst�lla att en 
    <i>
      text
    </i>
     �r identisk vid tv� olika tillf�llen ber�knas en kontrollsumma av 
    <i>
      texten
    </i>
    . Denna kontrollsumma �r 
    <i>
      alltid
    </i>
     densamma s� l�nge 
    <i>
      texten
    </i>
     inte har f�r�ndrats och d�rmed kan man garantera att 
    <i>
      texten
    </i>
     inte har f�r�ndrats �ver tid. Detta �r dock inte tillr�ckligt utan man beh�ver �ven kunna s�kerst�lla 
    <i>
      vem
    </i>
     som har utf�rdat 
    <i>
      texten
    </i>
    . Detta g�rs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan 
    <i>
      endast
    </i>
     g�ras med hj�lp av undertecknarens publika nyckel. Nu kan man med s�kerhet knyta en viss 
    <i>
      text
    </i>
     till en best�md utst�llare. 
  </p>
  <h2>
    <a name="Hur_anv�nder_jag_signeringstj�nsten?">
    </a>
    Hur anv�nder jag signeringstj�nsten?
    <a href="#Hur_anv�nder_jag_signeringstj�nsten?" class="section_anchor">
    </a>
  </h2>
  <p>
    F�rsta steget f�r en applikation �r att erbjuda signering f�r en anv�ndare. Efter detta tar signeringstj�nsten �ver och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det inneb�r, signeringstj�nsten tar �ven hand om certifikatskontrollen som bla. verifierar att certifikatet som anv�nds i signaturen inte �r sp�rrat. Det den nyttjande applikationen beh�ver ta h�nsyn till �r lagringen av signaturen. Nedan visas en schematisk bild �ver hur fl�det ser ut, de heldragna pilarna �r de interaktioner som m�ste hanteras/implementeras av applikationen. 
    <img src="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/images/Signatureservice-process.png"/>
  </p>
  <p>
    <a href="https://github.com/Vastra-Gotalandsregionen/oppna-program-signing-service/wiki/InDepth" rel="nofollow">
      L�s mer
    </a>
  </p>
</td>

  <p>
    <tt>
      oppna-program-signing-service
    </tt>
     ?r en del i V?stra G?talandsregionens satsning p? ?ppen k?llkod inom ramen f?r 
    <a href="https://github.com/Vastra-Gotalandsregionen//oppna-program">
      ?ppna Program
    </a>
    . 
  </p>