# EcoDala Android API Contract

This document describes the REST API shape expected by the EcoDala Android app.

Base URL used by Android:

- Debug emulator with ADB reverse: `http://127.0.0.1:8000/api/`
- Physical phone on the same Wi-Fi: use the laptop LAN IP, for example `http://192.168.1.10:8000/api/`
- Release: configure a public HTTPS domain.

Auth header for protected endpoints:

```http
Authorization: Bearer <access_token>
```

All timestamps should be ISO-8601 strings.

## Auth

### POST `/auth/login/`

Request:

```json
{
  "email": "user@mail.com",
  "password": "password123"
}
```

Response:

```json
{
  "access": "jwt_access_token",
  "refresh": "jwt_refresh_token",
  "user": {
    "id": "user-1",
    "email": "user@mail.com",
    "full_name": "Eco Warrior",
    "city": "Almaty",
    "university": "Satbayev University",
    "faculty": "Computer Science",
    "avatar": null,
    "eco_points": 845,
    "global_rank": 12,
    "level": 8,
    "created_at": "2026-06-01T10:00:00Z"
  }
}
```

### POST `/auth/register/`

Request:

```json
{
  "email": "user@mail.com",
  "password": "password123",
  "full_name": "Eco Warrior",
  "city": "Almaty",
  "university": "Satbayev University",
  "faculty": "Computer Science"
}
```

Response: same `user` object as above.

### GET `/auth/me/`

Response:

```json
{
  "id": "user-1",
  "email": "user@mail.com",
  "full_name": "Eco Warrior",
  "city": "Almaty",
  "university": "Satbayev University",
  "faculty": "Computer Science",
  "avatar": null,
  "eco_points": 845,
  "global_rank": 12,
  "level": 8,
  "total_recycled_kg": "31.5",
  "created_at": "2026-06-01T10:00:00Z",
  "updated_at": "2026-06-22T10:00:00Z"
}
```

Android calculates visual level from `eco_points`, but backend should still return `level` for consistency.

## Eco Rating And Points

Android rule:

- `100 EcoPoints = 1 level`
- `max level = 10`
- `level = min(eco_points / 100, 10)`
- `progress = eco_points % 100`

Backend should store every points action in a ledger.

### GET `/points/history/`

Response:

```json
{
  "count": 2,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": "evt-1",
      "source": "waste_submission",
      "title": "Plastic submitted",
      "points": 175,
      "created_at": "2026-06-22T09:00:00Z"
    },
    {
      "id": "evt-2",
      "source": "challenge_reward",
      "title": "Daily challenge completed",
      "points": 20,
      "created_at": "2026-06-22T12:00:00Z"
    }
  ]
}
```

Recommended sources:

- `waste_submission`
- `challenge_reward`
- `achievement_bonus`
- `report_verification`
- `photo_update`

## Map

All map objects must include `id`, `name`, `address`, `latitude`, `longitude`.

### GET `/recycling-points/?search=plastic`

Response:

```json
{
  "count": 1,
  "next": null,
  "previous": null,
  "results": [
    {
      "id": "point-1",
      "name": "Green Recycling Center",
      "address": "123 Eco Avenue, Almaty",
      "latitude": "43.238949",
      "longitude": "76.889709",
      "description": "Community recycling point",
      "working_hours": "08:00 - 19:00",
      "phone": "+7 777 123 4567",
      "accepted_categories": [
        {
          "id": "cat-plastic",
          "name": "Plastic",
          "slug": "plastic",
          "points_per_kg": 35,
          "icon": "recycling",
          "color_hex": "#2F8F3A"
        }
      ],
      "is_active": true
    }
  ]
}
```

### GET `/biotoilets/`

Query filters supported by Android:

- `free=true`
- `accessible=true`
- `open_now=true`

Response item:

```json
{
  "id": "toilet-1",
  "name": "Central Park Biotoilet",
  "photo": "https://example.com/photo.jpg",
  "address": "Central Park, Almaty",
  "latitude": "43.250000",
  "longitude": "76.950000",
  "opening_hours": "08:00 - 22:00",
  "status": "open",
  "type": "free",
  "is_accessible": true,
  "is_family_friendly": true,
  "cleanliness_rating": 4.6,
  "review_count": 24
}
```

Allowed `status`: `open`, `unknown`, `closed`, `maintenance`.
Allowed `type`: `free`, `paid`.

### GET `/water-stations/`

Query filters supported by Android:

- `free=true`
- `open_now=true`
- `refill=true`

Response item:

```json
{
  "id": "water-1",
  "name": "University Refill Station",
  "photo": "https://example.com/photo.jpg",
  "address": "University main hall",
  "latitude": "43.240000",
  "longitude": "76.910000",
  "working_hours": "08:00 - 20:00",
  "water_type": "refill_station",
  "status": "available",
  "rating": 4.8,
  "review_count": 11
}
```

Allowed `water_type`: `free_drinking_water`, `refill_station`, `filtered_water`, `water_dispenser`, `bottled_water_vending_machine`.
Allowed `status`: `available`, `unknown`, `temporarily_unavailable`, `maintenance`.

### GET `/eco-reports/`

Query filters supported by Android:

- `status=submitted`
- `severity=high`

Response item:

```json
{
  "id": "report-1",
  "title": "Illegal dumping near river",
  "photo": "https://example.com/photo.jpg",
  "address": "Ile district, Almaty region",
  "latitude": "43.320000",
  "longitude": "76.870000",
  "waste_description": "Plastic bags and construction waste",
  "status": "submitted",
  "severity": "high",
  "reported_by_name": "Eco Warrior",
  "verification_count": 3,
  "created_at": "2026-06-22T09:00:00Z",
  "comments": []
}
```

Allowed `status`: `submitted`, `verified`, `in_progress`, `resolved`, `rejected`.
Allowed `severity`: `low`, `medium`, `high`.

### POST `/eco-reports/{id}/verify/`

Response: updated eco report item.

## Waste Submission

### GET `/waste-categories/`

Response item:

```json
{
  "id": "cat-plastic",
  "name": "Plastic",
  "slug": "plastic",
  "description": "Plastic bottles, packages",
  "points_per_kg": 35,
  "icon": "recycling",
  "color_hex": "#2F8F3A"
}
```

Allowed slugs: `plastic`, `paper`, `glass`, `batteries`, `electronics`, `organic`, `metal`.

### POST `/submit-waste/`

Request:

```json
{
  "category": "cat-plastic",
  "recycling_point": "point-1",
  "weight_kg": "5.0",
  "comment": "Five plastic bottles"
}
```

Response:

```json
{
  "id": "submission-1",
  "category": "cat-plastic",
  "category_detail": {
    "id": "cat-plastic",
    "name": "Plastic",
    "slug": "plastic",
    "points_per_kg": 35
  },
  "recycling_point": "point-1",
  "weight_kg": "5.0",
  "photo": null,
  "comment": "Five plastic bottles",
  "status": "pending",
  "points_awarded": 0,
  "created_at": "2026-06-22T09:00:00Z",
  "updated_at": "2026-06-22T09:00:00Z"
}
```

Recommended status flow:

- `pending`: user submitted, no points yet
- `approved`: admin approved, points awarded
- `rejected`: no points

## Challenges

### GET `/challenges/?type=daily`

Response item:

```json
{
  "id": "challenge-1",
  "title": "Recycle 5 Plastic Bottles",
  "description": "Submit plastic waste today",
  "target_kg": "1.0",
  "reward_points": 20,
  "starts_at": "2026-06-22T00:00:00Z",
  "ends_at": "2026-06-22T23:59:59Z",
  "status": "daily",
  "progress": "0.5",
  "is_completed": false,
  "created_at": "2026-06-20T09:00:00Z"
}
```

Allowed `status`: `daily`, `weekly`, `special`.

## Achievements

### GET `/achievements/`

Response item:

```json
{
  "id": "achievement-1",
  "code": "first_recycling",
  "title": "First Recycling",
  "description": "Submit your first waste item",
  "icon": "recycling",
  "rule": {
    "metric": "submissions_count",
    "threshold": 1
  },
  "unlocked": true,
  "created_at": "2026-06-22T09:00:00Z"
}
```

## Leaderboard

### GET `/leaderboard/`

Response:

```json
[
  {
    "rank": 1,
    "id": "user-2",
    "full_name": "Aibek",
    "city": "Almaty",
    "university": "Satbayev University",
    "faculty": "Computer Science",
    "eco_points": 1200,
    "level": 10
  }
]
```

Backend should return sorted users. Android will still sort by points as a fallback.

## AI Waste Scanner

### POST `/ai-waste-scanner/analyze/`

Current Android demo sends form field:

```http
provider=demo
```

Response:

```json
{
  "id": "scan-1",
  "image": null,
  "provider": "demo",
  "predicted_category": {
    "id": "cat-plastic",
    "name": "Plastic",
    "slug": "plastic",
    "points_per_kg": 35
  },
  "confidence": 0.87,
  "advice": "This looks recyclable. Take it to a plastic collection point.",
  "created_at": "2026-06-22T09:00:00Z"
}
```

Later production version should accept multipart image upload.

## Error Format

Recommended error response:

```json
{
  "detail": "Human readable error message",
  "code": "validation_error",
  "fields": {
    "email": ["This field is required."]
  }
}
```

Android currently shows `detail` or the network error message.
