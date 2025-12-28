package com.devbuild.enums;

public enum SoutenanceStatus {
    DRAFT,          // brouillon par le doctorant
    SUBMITTED,      // demande envoyée
    UNDER_REVIEW,   // directeur / admin en train d'étudier
    REJECTED,       // refusée
    APPROVED,       // prérequis OK, autorisation donnée
    SCHEDULED,      // date/heure/lieu fixés
    DEFENDED,       // soutenance réalisée
    CLOSED          // dossier clos
}