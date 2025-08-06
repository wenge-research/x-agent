package com.wenge.model.strategy.matterGuide;

import com.wenge.model.entity.MatterGuideFiled;

import java.util.List;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2024/7/17 9:46
 */

public class MatterGuideStrategyFactory {

    private static final String PEOPLE_PROFILE = "2";

    public static MatterGuideStrategy createStrategy(String matterId, List<MatterGuideFiled> guideFileds) {
        MatterGuideStrategy<?> matterGuideStrategy = new PeopleProfileImpl(matterId,guideFileds);
        /*switch (matterId) {
            case PEOPLE_PROFILE:
                matterGuideStrategy = new PeopleProfileImpl(matterId,guideFileds);
                break;
            case "3":
                matterGuideStrategy = new PeopleProfileImpl(matterId,guideFileds);
                break;
            case "5":
                matterGuideStrategy = new PeopleProfileImpl(matterId,guideFileds);
                break;
            default:
                throw new IllegalArgumentException();
        }*/
        return matterGuideStrategy;
    }
}