package com.game.controller;

import com.game.entity.*;
import com.game.service.PlayerService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping(value = "/players",method = RequestMethod.GET)
    public List<Player> getAll(@RequestParam(value = "name", required = false) String name,
                               @RequestParam(value = "title", required = false) String title,
                               @RequestParam(value = "race", required = false) Race race,
                               @RequestParam(value = "profession", required = false) Profession profession,
                               @RequestParam(value = "after", required = false) Long after,
                               @RequestParam(value = "before", required = false) Long before,
                               @RequestParam(value = "banned", required = false) Boolean banned,
                               @RequestParam(value = "minExperience", required = false) Integer minExperience,
                               @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                               @RequestParam(value = "minLevel", required = false) Integer minLevel,
                               @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                               @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                               @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                               @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        Specification<Player> specification = Specification.where(playerService.filterByName(name)
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByBirthday(after, before))
                .and(playerService.filterByBanned(banned))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel)));

        return playerService.getAll(specification, pageable).getContent();
    }

    @RequestMapping(value = "/players/count", method = RequestMethod.GET)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "title", required = false) String title,
                            @RequestParam(value = "race", required = false) Race race,
                            @RequestParam(value = "profession", required = false) Profession profession,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "banned", required = false) Boolean banned,
                            @RequestParam(value = "minExperience", required = false) Integer minExperience,
                            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                            @RequestParam(value = "minLevel", required = false) Integer minLevel,
                            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        Specification<Player> specification = Specification.where(playerService.filterByName(name)
                .and(playerService.filterByTitle(title))
                .and(playerService.filterByRace(race))
                .and(playerService.filterByProfession(profession))
                .and(playerService.filterByBirthday(after, before))
                .and(playerService.filterByBanned(banned))
                .and(playerService.filterByExperience(minExperience, maxExperience))
                .and(playerService.filterByLevel(minLevel, maxLevel)));

        return playerService.getAll(specification, Pageable.unpaged()).getNumberOfElements();
    }

    @PostMapping(value = "/players")
    public @ResponseBody Player create(@RequestBody Player player) {
        try {
            playerService.create(player);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
        return player;
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
        playerService.delete(id);
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.GET)
    public @ResponseBody Player getById(@PathVariable("id") Long id) {
        return playerService.getById(id);
    }

    @PostMapping(value = "/players/{id}")
    public Player edit(@PathVariable("id") Long id,
                       @RequestBody Player player) {

        return playerService.update(id, player);
    }
}