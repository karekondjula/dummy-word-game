# dummy-word-game

Dummy word game is an assignment project based on AIDL communication between two projects.

## User part

The user part shows the simple UI associated with the Dummy word game. The user's MainActivity
is created with no additional logic, as "dumb" as possible. It captures the input text via a submit
event and dispatches it off to GameManager. Using an additional Fragment seemed like an overkill.

GameManager is the main handler of actions. The ServiceManager object is created inside GameManager.
The creation of GameManager and ServiceManager follow a simplified dependency injection pattern. In
ServiceManager the LifecycleEvent API is used to listen to onStart() and onStop() events which are
the triggers for binding and unbinding the bot service. ServiceManager dispatches the reply from
the bot using a Flow API and the GameManager dispatches the GameState events using LiveData.

The GameManager stores just the validated last move and uses it to analyze if the next move by the
bot or the user is valid.

When the game finishes an alert dialog shows who won and what was the error.

## Bot part

The bot part shows no UI (actually shows an activity which uses a transparent theme) and is used
only for starting the BotService which the user part binds using AIDL. The BotService receives from
the user part the text (a game move by the user) , prepends *b* , flattens it with removing the
whitespaces and through a callback sends it back to the user. There is a 3% chance that the reply
will be *"TOO_MUCH_FOR_ME"* which will force the bot to lose the game.