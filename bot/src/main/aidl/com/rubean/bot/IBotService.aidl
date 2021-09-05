package com.rubean.bot;
import com.rubean.bot.IBotCallback;

interface IBotService {
    void nextUserMove(String move);
    void registerCallback(IBotCallback callback);
}