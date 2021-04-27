package com.game.service;

import com.game.entity.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PlayerService {
    Page<Player> getAll(Specification<Player> specification, Pageable pageable);
    Player create(Player player);
    void delete(Long id);
    Player getById(Long id);
    Player update(Long id, Player player);

    Specification<Player> filterByName(String name);
    Specification<Player> filterByTitle(String name);
    Specification<Player> filterByRace(Race race);
    Specification<Player> filterByProfession(Profession profession);
    Specification<Player> filterByBirthday(Long after, Long before);
    Specification<Player> filterByBanned(Boolean banned);
    Specification<Player> filterByExperience(Integer minExperience, Integer maxExperience);
    Specification<Player> filterByLevel(Integer minLevel, Integer maxLevel);
}