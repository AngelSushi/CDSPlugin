package com.angelsushi.cds.manager;

public class Message {

    public static final String BUY_HOUSE = "§e[CDS] §fUne maison vient d'être créer";
    public static final String SELL_HOUSE = "§e[CDS] §fUne maison vient d'être supprimée";
    public static final String CREATE_TEAM = "§e[CDS] §fUne équipe vient d'être créer";
    public static final String JOIN_TEAM = "§e[CDS] §fUn joueur vient de rejoindre l'équipe";
    public static final String INVITE_TEAM = "§e[CDS] §fUn joueur vient d'être inviter dans l'équipe";
    public static String ADD_MONEY_BANK = "§e[CDS] §fVous venez d'ajouter §a%n éméraudes §f à votre banque";
    public static String WITHDRAW_MONEY = "§e[CDS] §fVous venez de retirer §a%n éméraudes §f de votre banque";
    public static String ADD_BEGINDATE = "§e[CDS] §fVous venez de définir la date de lancement au %d %m %y à %h:%m";
    public static String SAVE_MOD = "§e[CDS] §fLe mode de sauvegarde vient de changer.";
    public static String CREATE_HOUSE = "§e[CDS] §f La maison §e%n §f vient d'être crée.";
    public static String DELETE_HOUSE = "§e[CDS] §f La maison §e%n §f vient d'être supprimée.";

    public static String CREATE_VILLAGER_BANK = "§e[CDS]§f Vous venez de créer un villageois banque aux coordonnées x: §e%x§f y: §e%y§f, z:§e%z§f";
    public static String ADD_MERCHANT = "§e[CDS] §fVous venez de créer un marchand (§e%n§f) aux coordonnées x: §e%x§f y: §e%y§f, z:§e%z§f";

    public static String EVENT_START = "§e[CDS] §fLancement de l'évènement. Fin dans §e%n§f jours";
    public static String EVENT_STOP = "§e[CDS] §fFin de l'évènement. Félicitations aux gagnants: ";

    public static String ERROR_MESSAGE = "§a[CDS] Une erreur est survenue. Vous ne pouvez pas faire cette action";
}
