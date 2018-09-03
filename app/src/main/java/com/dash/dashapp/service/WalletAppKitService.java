package com.dash.dashapp.service;

import android.app.Service;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.listeners.PeerConnectedEventListener;
import org.bitcoinj.core.listeners.PeerDisconnectedEventListener;
import org.bitcoinj.core.listeners.PeerDiscoveredEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class WalletAppKitService extends Service {

    private static final String TAG = WalletAppKitService.class.getCanonicalName();

    private WalletAppKit kit;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public WalletAppKitService getService() {
            return WalletAppKitService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initWalletAppKit();
        return Service.START_NOT_STICKY;
    }

    private void initWalletAppKit() {
        Log.d(TAG, "WalletAppKitService.initWalletAppKit()");

//        BriefLogFormatter.init();
        initLogging();

        boolean testnetMode = false;
        String filePrefix = testnetMode ? "testnet" : "mainnet";
        NetworkParameters params = testnetMode ? TestNet3Params.get() : MainNetParams.get();

        File walletAppKitDir = getApplication().getDir("walletappkit", Context.MODE_PRIVATE);
        // Start up a basic app using a class that automates some boilerplate. Ensure we always have at least one key.
        kit = new WalletAppKit(params, walletAppKitDir, filePrefix, false) {
            @Override
            protected void onSetupCompleted() {
                Log.d(TAG, "WalletAppKit.onSetupCompleted()");
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }
                init();
            }

        };

        // Download the block chain and wait until it's done.
        kit.startAsync();
//        kit.awaitRunning();
    }

    private MutableLiveData<List<Peer>> peerListData;

    public void setPeerListData(MutableLiveData<List<Peer>> peerListData) {
        this.peerListData = peerListData;
    }

    private void init() {
        kit.peerGroup().addDiscoveredEventListener(new PeerDiscoveredEventListener() {
            @Override
            public void onPeersDiscovered(Set<PeerAddress> peerAddresses) {
                if (peerListData != null && peerListData.hasActiveObservers()) {
                    peerListData.postValue(kit.peerGroup().getConnectedPeers());
                }
            }
        });
//        kit.peerGroup().addChainDownloadStartedEventListener(new ChainDownloadStartedEventListener() {
//            @Override
//            public void onChainDownloadStarted(Peer peer, int blocksLeft) {
//                Log.d(TAG, "kit.peerGroup().onChainDownloadStarted" + peer);
//            }
//        });
        kit.peerGroup().addConnectedEventListener(new PeerConnectedEventListener() {
            @Override
            public void onPeerConnected(Peer peer, int peerCount) {
                Log.d(TAG, "kit.peerGroup().onPeerConnected" + peer);
            }
        });
        kit.peerGroup().addDisconnectedEventListener(new PeerDisconnectedEventListener() {
            @Override
            public void onPeerDisconnected(Peer peer, int peerCount) {
                Log.d(TAG, "kit.peerGroup().onPeerDisconnected" + peer);
            }
        });
//        kit.peerGroup().addBlocksDownloadedEventListener(new DownloadProgressTracker() {
//            @Override
//            protected void progress(double pct, int blocksSoFar, Date date) {
//                Log.d(TAG, String.format(Locale.US, "Chain download %d%% done with %d blocks to go, block date %s", (int) pct, blocksSoFar, Utils.dateTimeFormat(date)));
//            }
//        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initLogging() {
        final File logDir = getDir("log", MODE_PRIVATE);
        final File logFile = new File(logDir, "wallet.log");

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        final PatternLayoutEncoder filePattern = new PatternLayoutEncoder();
        filePattern.setContext(context);
        filePattern.setPattern("%d{HH:mm:ss,UTC} [%thread] %logger{0} - %msg%n");
        filePattern.start();

        final RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
        fileAppender.setContext(context);
        fileAppender.setFile(logFile.getAbsolutePath());

        final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        rollingPolicy.setContext(context);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern(logDir.getAbsolutePath() + "/wallet.%d{yyyy-MM-dd,UTC}.log.gz");
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.start();

        fileAppender.setEncoder(filePattern);
        fileAppender.setRollingPolicy(rollingPolicy);
        fileAppender.start();

        final PatternLayoutEncoder logcatTagPattern = new PatternLayoutEncoder();
        logcatTagPattern.setContext(context);
        logcatTagPattern.setPattern("%logger{0}");
        logcatTagPattern.start();

        final PatternLayoutEncoder logcatPattern = new PatternLayoutEncoder();
        logcatPattern.setContext(context);
        logcatPattern.setPattern("[%thread] %msg%n");
        logcatPattern.start();

        final LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(context);
        logcatAppender.setTagEncoder(logcatTagPattern);
        logcatAppender.setEncoder(logcatPattern);
        logcatAppender.start();

        final ch.qos.logback.classic.Logger log = context.getLogger(Logger.ROOT_LOGGER_NAME);
        log.addAppender(fileAppender);
        log.addAppender(logcatAppender);
        log.setLevel(Level.INFO);
    }
}
