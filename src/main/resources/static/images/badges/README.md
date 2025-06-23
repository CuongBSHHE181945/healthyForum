# Badge Icons Directory

This directory contains badge icons for the Healthy Forum application.

## Directory Structure

```
badges/
├── unlocked/          # Icons for unlocked badges
│   ├── badge1.png
│   ├── badge2.png
│   └── ...
└── locked/            # Icons for locked badges (grayscale versions)
    ├── badge1_locked.png
    ├── badge2_locked.png
    └── ...
```

## Usage in Database

When adding badges to the database, use these paths:

- **Unlocked badges**: `/images/badges/unlocked/badge_name.png`
- **Locked badges**: `/images/badges/locked/badge_name_locked.png`

## Example Database Entries

```sql
INSERT INTO badge (name, description, icon, locked_icon) VALUES 
('First Post', 'Created your first post', '/images/badges/unlocked/first_post.png', '/images/badges/locked/first_post_locked.png'),
('Health Champion', 'Completed 10 health assessments', '/images/badges/unlocked/health_champion.png', '/images/badges/locked/health_champion_locked.png');
```

## Image Requirements

- **Format**: PNG recommended (supports transparency)
- **Size**: 64x64 pixels (as defined in CSS)
- **Style**: 
  - Unlocked: Full color
  - Locked: Grayscale version of the same icon

## Access in Templates

The icons are automatically served by Spring Boot's static resource handler and can be accessed directly in Thymeleaf templates using the paths stored in the database. 