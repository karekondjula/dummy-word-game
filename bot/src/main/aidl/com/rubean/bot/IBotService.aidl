package com.rubean.bot;
import com.rubean.bot.IBotCallback;

interface IBotService {
    void nextUserMove(in List<String> words);
    void registerCallback(IBotCallback callback);
}