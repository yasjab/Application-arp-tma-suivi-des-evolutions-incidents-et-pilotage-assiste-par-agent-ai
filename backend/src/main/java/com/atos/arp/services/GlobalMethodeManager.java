package com.atos.arp.services;

public class GlobalMethodeManager {

    /**
     * isDI() — la catégorie correspond à une DI, pas une FC
     * Basé sur les vraies catégories dans la table categorie :
     * "DI Livrées (FIXE)", "DI Livrées (MOBILE)", "DI Encours (FIXE)", etc.
     */
    public boolean isDI(String categorie) {
        if (categorie == null || categorie.isEmpty()) return false;
        String lower = categorie.toLowerCase().trim();
        // Uniquement les catégories qui commencent explicitement par "di "
        return lower.startsWith("di ");
    }

    /**
     * isNotFC() — la catégorie n'est PAS une FC pure
     * Dans l'ancienne appli, cette méthode retourne true pour les catégories
     * qui ne peuvent jamais être des FC Livrées (ex: DI Evolutif, etc.)
     * Dans notre cas : retourne false pour tout sauf DI explicites
     * → Laisse passer les catégories FC Evolutif, FC Correctif, etc.
     */
    public boolean isNotFC(String categorie) {
        if (categorie == null || categorie.isEmpty()) return false;
        String lower = categorie.toLowerCase().trim();
        // Catégories qui sont définitivement des DI (pas des FC)
        return lower.startsWith("di ") || lower.startsWith("demande d'intervention");
    }
}