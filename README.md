<p align="center">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://i.ibb.co/BqYkwTs/Logo-universita-firenze.png">
  <img alt="UniFi Logo" src="https://i.ibb.co/3FNRHSD/Logo-universita-firenze.png" width="300">
</picture>
</p>

# Extension of the Sirio Library

The goal of this project is to create an extension of the [SIRIO](https://github.com/oris-tool/sirio) library that allows to represent preemptive Time Petri Nets (pTPN) with priorities that are expressions of a marking, as well as model a schema for Earliest Deadline First (EDF) and Rate Monotonic (RM) scheduling. Specifically, the task metamodel must be modified to allow it to be composed of one or more computational chunks, and an implementation of the Priority Ceiling Emulation Protocol (PCEP) must be provided as a policy for resource management, ensuring mutual exclusion.

The library produced by [1], which is based on the work of [2], has been extended.

## Author

* Alessio Bugetti, <a href="mailto:alessio.bugetti@edu.unifi.it">alessio.bugetti@edu.unifi.it</a>

## References

[1] L. Leuter, *Software Engineering for Embedded Systems Relazione di Progetto*, 2021/2022.

[2] L. Macchiarini, *≪Design and development of a software component for the analysis of preemptive timed models≫* Tesi di Laurea Triennale in Ingegneria Informatica, Università degli Studi di Firenze, 2018/2019.
