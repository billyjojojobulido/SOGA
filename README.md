# SOGA: A Scavenger-Hunt Online Game App
A **scavenger hunt** is a recreational activity or game in which participants, often in teams, are given a list of items to find or tasks to complete within a specific time frame. The goal of the game is to locate all the items or accomplish all the tasks on the list before the time runs out. Scavenger hunts can be organized for various purposes, including team-building exercises, educational activities, parties, or simply as a fun and challenging way to engage with a group of people.

**SOGA**  is an app for creating, sharing, and playing scavenger hunts of your own design. It does nothing on its own, but it serves as a useful tool for groups to create and organize their own game.



## Tutorial

#### **Tutorial 07 - Group 01**



## Stakeholders

| Name          | Preferred Name | SID     |
| ------------- | -------------- | ------- |
| Baocheng Wang | Billy          | 1410106 |
| Elena Li      | Elena          | 1347015 |
| Jiafei Tan    | Felix          | 1308273 |
| Jonathan Thai | Jono           | 1085295 |
| Yanhao Xu     | /              | 1400170 |



## Key Technologies

- Java (Android Studio)
- Firebase (cloud database) / SQLite (only if necessary)?
- HTTP requesting external API (RESTful APIs)
  - Google Map API
  - Weather API (only if necessary)

## Main Features

- User Management (Firebase)
  - [x] Registration
  - [x] Login/out
- Gaming
  - [x] Room Management
    - [x] Create
    - [x] Join (room code)
  - Game Setup
    - [x] Tasks Managment
      - [x] Positioning
      - [x] Small Games
      - [x] Evidences Editing
    - [x] Destination (Final Task)
      - [x] Find on map (Google Map API) 
  - [x] Player
    - [x] Constraint on
      - [x] Hints (depends on further design)
        - [x] Naive approach: give out the address
    - [x] Arrival Check
      - [x] Button on the Screen
      - [x] Disance check
      - [x] Small Games Completion
      - [x] History board
    - History Board
      - [x] show time per user 
      - [x] show steps
