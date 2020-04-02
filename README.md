# Operating-systems

### Atlieka :
Simonas Stipinas
Marius Kriukovas

### Aprašas :

Projektuojama interaktyvi OS.
Virtualios mašinos procesoriaus komandos operuoja su duomenimis, esančiais steko viršūnėje. Yra komandos duomenų persiuntimui iš atminties į steką ir atvirkščiai, aritmetinės (sudėties, atimties, daugybos, dalybos), sąlyginio ir besąlyginio valdymo perdavimo, įvedimo, išvedimo ir programos pabaigos komandos. Registrai yra du: komandų skaitiklio ir steko viršūnės. Atminties dydis yra 256 blokų po 256 žodžius (žodžio ilgį pasirinkite patys).
Realios mašinos procesorius gali dirbti dviem režimais: vartotojo ir supervizoriaus. Virtualios mašinos atmintis atvaizduojama į vartotojo atmintį naudojant puslapių transliaciją. Numatytas swapping mechanizmo palaikymas, t.y. vartotojo atminties puslapiai gali būti iškeliami į išorinę atminti. Yra taimeris, kas tam tikrą laiko intervalą generuojantis pertraukimus. Įvedimui naudojama klaviatūra, išvedimui - ekranas. Yra išorinės atminties įrenginys - kietasis diskas.
Vartotojas, dirbantis su sistema, programas paleidžia interaktyviai, surinkdamas atitinkamą komandą. Laikoma, kad vartotojo programos yra realios mašinos kietajame diske, į kurį jos patalpinamos „išorinėmis“, modelio, o ne projektuojamos OS, priemonėmis. Trūkstant atminties, sistema dalį vartotojo atminties puslapių (bet ne visą kažkurios VM atmintį) perkelia į išorinę atmintį, t.y. naudoja swapping'ą.

### Užduotis :

Virtualios mašinos projekte turi būti realizuota:
Parašyta bent viena pavyzdinė programa, kurią reiks naudoti pristatant virtualią mašiną atsiskaitymo metu.
Galimybė įvykdyti programą iš karto arba vykdyti ją žingsniniu rėžimu.
Vartotojo sąsajoje turi būti komandų atlikimo demonstracija bei visų VM ir RM komponentų būsenų kaita vykdant programą žingsniniu rėžimu.
Registrų reikšmės (VM ir RM).
Sekanti vykdoma komanda.
Išorinių įrenginių būsenos.
Turi būti galimybė atvaizduoti VM atmintį.
Turi būti galimybė atvaizduoti RM atmintį arba nurodytą RM atminties puslapį.
Puslapiai VM skiriami ne nuosekliai
