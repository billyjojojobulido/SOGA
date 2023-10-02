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



## Key Technologies:

- Java (Android Studio)
- Firebase (cloud database) / SQLite (only if necessary)?
- HTTP requesting external API (RESTful APIs)
  - Google Map API
  - Weather API (only if necessary)



## Main Features: (MFP)

- User Management (Firebase)
  - Registration 
  - Login/out
  - Forget Password
- Gaming
  - Room Management
    - Create & Cancel
    - Join (room code)
  - Game Setup
    - Tasks Managment
      - Positioning
      - Small Games
      - Evidences Editing (Text & Image [only if necessary])
    - Destination (Final Task)
      - Find on map (Google Map API) 
  - Player
    - Location-based Progress Check
      - Every 5 mins, check if the player is getting closer to the current target, if so, message (“Good job”), else, (“Oops...”)
    - Constraint on
      - Timeout (Quit or Keep failing)
      - Hints (depends on further design)
        - Naive approach: give out the address
        - Fancier approach: take out a few letters from the address
    - Arrival Check
      - Button on the Screen
      - Disance check
      - Small Games Completion
      - Leaderboard
    - Leaderboard
      - algorithm-based 
      - show steps
      - Commemorative Photos (only if necessary)
