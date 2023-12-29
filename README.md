<p align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://i.ibb.co/BqYkwTs/Logo-universita-firenze.png">
  <img alt="Shows an illustrated sun in light color mode and a moon with stars in dark color mode." src="https://i.ibb.co/3FNRHSD/Logo-universita-firenze.png" width="300">
</picture>
</p>

# Estensione della libreria Sirio

L’obbiettivo di questo progetto è quello di realizzare un'estensione della libreria [SIRIO](https://github.com/oris-tool/sirio) che
consenta di rappresentare preemptive Time Petri Net (pTPN) con le priorità che sono espressioni di una marcatura e che permetta di modellare uno schema per lo scheduling Earliest Deadline First (EDF) e per Rate Monotonic (RM). Nello specifico, deve essere modificato il metamodello dei task per permettere di essere composti da uno o più chunk computazionali e inoltre deve essere fornita un'implementazione del Priority Ceiling Emulation Protocol (PCEP) come politica di gestione delle risorse in mutua esclusione.

È stata estesa la libreria prodotta da [1] che è basata sul lavoro di [2].

## Autore

* Alessio Bugetti, <a href="mailto:alessio.bugetti@edu.unifi.it">alessio.bugetti@edu.unifi.it</a>

## Riferimenti

[1] L. Leuter, *Software Engineering for Embedded Systems Relazione di Progetto*, 2021/2022.

[2] L. Macchiarini, *≪Design and development of a software component for the analysis of preemptive timed models≫* Tesi di Laurea Triennale in Ingegneria Informatica, Università degli Studi di Firenze, 2018/2019.
