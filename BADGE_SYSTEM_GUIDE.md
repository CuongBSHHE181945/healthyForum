# Badge System Implementation Guide

## Overview
This guide explains how to implement and test the badge system in the Healthy Forum application.

## Files Created

### 1. Database Structure
- `badge_test_data.sql` - Complete test data for the badge system
- Tables: `badge_source_type`, `badge`, `badge_requirement`, `user_badge`

### 2. Icon Directory Structure
```
healthyForum/src/main/resources/static/images/badges/
├── unlocked/          # Unlocked badge icons (64x64 PNG)
├── locked/            # Locked badge icons (grayscale versions)
└── README.md          # Icon usage documentation
```

## How to Set Up

### Step 1: Run the Database Script
```sql
-- Execute the badge test data script
source badge_test_data.sql;
```

### Step 2: Add Badge Icons
Replace the placeholder files in the `images/badges/` directories with actual PNG images:

**Required Icons:**
- `first_post.png` & `first_post_locked.png`
- `active_member.png` & `active_member_locked.png`
- `forum_veteran.png` & `forum_veteran_locked.png`
- `community_leader.png` & `community_leader_locked.png`
- `sleep_tracker.png` & `sleep_tracker_locked.png`
- `sleep_champion.png` & `sleep_champion_locked.png`
- `sleep_master.png` & `sleep_master_locked.png`
- `quality_sleeper.png` & `quality_sleeper_locked.png`
- `meal_planner.png` & `meal_planner_locked.png`
- `nutrition_expert.png` & `nutrition_expert_locked.png`
- `healthy_chef.png` & `healthy_chef_locked.png`
- `balanced_diet.png` & `balanced_diet_locked.png`
- `health_conscious.png` & `health_conscious_locked.png`
- `health_champion.png` & `health_champion_locked.png`
- `wellness_expert.png` & `wellness_expert_locked.png`
- `health_guru.png` & `health_guru_locked.png`
- `messenger.png` & `messenger_locked.png`
- `social_butterfly.png` & `social_butterfly_locked.png`
- `communication_pro.png` & `communication_pro_locked.png`
- `helpful_user.png` & `helpful_user_locked.png`
- `community_helper.png` & `community_helper_locked.png`
- `blogger.png` & `blogger_locked.png`
- `content_creator.png` & `content_creator_locked.png`
- `influencer.png` & `influencer_locked.png`
- `regular_user.png` & `regular_user_locked.png`
- `dedicated_member.png` & `dedicated_member_locked.png`
- `loyal_supporter.png` & `loyal_supporter_locked.png`

## Badge Categories

### 1. Community Engagement (4 badges)
- **First Post**: First forum post
- **Active Member**: 10 posts
- **Forum Veteran**: 50 posts
- **Community Leader**: 100 posts

### 2. Sleep Tracking (4 badges)
- **Sleep Tracker**: First sleep entry
- **Sleep Champion**: 7 consecutive days
- **Sleep Master**: 30 consecutive days
- **Quality Sleeper**: 8+ hours for 5 days

### 3. Meal Planning (4 badges)
- **Meal Planner**: First meal plan
- **Nutrition Expert**: 10 meal plans
- **Healthy Chef**: 25 meal plans
- **Balanced Diet**: 7 consecutive days

### 4. Health Assessment (4 badges)
- **Health Conscious**: First assessment
- **Health Champion**: 5 assessments
- **Wellness Expert**: 10 assessments
- **Health Guru**: 20 assessments

### 5. Communication (3 badges)
- **Messenger**: First message
- **Social Butterfly**: 25 messages
- **Communication Pro**: 100 messages

### 6. Feedback (2 badges)
- **Helpful User**: First feedback
- **Community Helper**: 5 feedback items

### 7. Blog (3 badges)
- **Blogger**: First blog post
- **Content Creator**: 5 blog posts
- **Influencer**: 10 blog posts

### 8. Loyalty (3 badges)
- **Regular User**: 7 consecutive login days
- **Dedicated Member**: 30 consecutive login days
- **Loyal Supporter**: 100 consecutive login days

## Test Users and Their Badges

### Sarah (userID: 1) - Active User
- **Displayed Badges**: First Post, Active Member, Sleep Tracker
- **Unlocked Badges**: Sleep Champion, Meal Planner, Health Conscious, Messenger, Helpful User, Regular User
- **Total**: 9 badges

### Alice (userID: 2) - Moderate User
- **Displayed Badges**: First Post, Sleep Tracker
- **Unlocked Badges**: Meal Planner, Messenger, Regular User
- **Total**: 5 badges

### Bob (userID: 3) - New User
- **Displayed Badges**: First Post
- **Unlocked Badges**: Sleep Tracker
- **Total**: 2 badges

### Carol (userID: 4) - Experienced User
- **Displayed Badges**: First Post, Active Member, Sleep Tracker
- **Unlocked Badges**: Forum Veteran, Sleep Champion, Meal Planner, Health Conscious, Messenger, Helpful User, Blogger, Regular User
- **Total**: 11 badges

## Testing the Badge System

### 1. View Profile Page
- Navigate to `/user-profile` (requires login)
- You should see displayed badges at the top
- Unlocked badges below (draggable)
- Locked badges at the bottom (grayscale)

### 2. Badge Detail Page
- Click on any badge to view details
- URL: `/badge/{badge_id}`
- Shows badge requirements and unlock status

### 3. Drag and Drop Functionality
- Drag unlocked badges to the display zone
- Badges should move to the displayed section
- Page should reload to show changes

## Database Queries for Testing

### View All Badges
```sql
SELECT * FROM badge ORDER BY id;
```

### View User Badges
```sql
SELECT u.username, b.name, ub.earned_at, ub.displayed
FROM user_badge ub
JOIN user u ON ub.userID = u.userID
JOIN badge b ON ub.badge_id = b.id
WHERE u.username = 'sarah1';
```

### View Badge Requirements
```sql
SELECT b.name, bst.name as source_type, br.value
FROM badge_requirement br
JOIN badge b ON br.badge_id = b.id
JOIN badge_source_type bst ON br.source_type_id = bst.id
ORDER BY b.id;
```

## Implementation Notes

### Icon Requirements
- **Size**: 64x64 pixels (as defined in CSS)
- **Format**: PNG (supports transparency)
- **Style**: 
  - Unlocked: Full color
  - Locked: Grayscale version

### Badge Unlocking Logic
The system tracks various user activities:
- Post counts
- Sleep tracking days
- Meal plan creation
- Health assessment completion
- Message sending
- Feedback submission
- Blog post creation
- Login streaks

### Display System
- Users can display up to 3 badges on their profile
- Badges can be dragged and dropped to change display
- Locked badges show grayscale versions

## Next Steps

1. **Add Real Icons**: Replace placeholder files with actual badge images
2. **Implement Badge Logic**: Create services to check and award badges
3. **Add Badge Notifications**: Notify users when they earn new badges
4. **Create Badge Progress**: Show progress towards unlocking badges
5. **Add Badge Statistics**: Track badge distribution across users 