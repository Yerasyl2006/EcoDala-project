# EcoDala Backend Handoff

This file is for the backend developer. It describes what the Android app expects from the REST API.

## Project State

Android side is ready for backend integration with:

- Kotlin + Jetpack Compose
- Retrofit + Moshi
- JWT access/refresh token flow
- Offline cache fallback for map/profile/history
- Multipart image upload for waste submissions, eco reports and AI scanner
- Google Maps production API key config via local properties or environment variables
- Release signing config via local `keystore.properties` or environment variables

Base URL is configured in Android through Gradle:

- Debug default: `http://127.0.0.1:8000/api/`
- Release default: `https://api.ecodala.kz/api/`
- Can be overridden with:
  - `ECODALA_DEBUG_API_BASE_URL`
  - `ECODALA_RELEASE_API_BASE_URL`

For a real phone on local Wi-Fi, use laptop LAN IP, for example:

```properties
ECODALA_DEBUG_API_BASE_URL=http://192.168.1.10:8000/api/
```

## Auth

Android sends:

```http
Authorization: Bearer <access_token>
```

### POST `/auth/login/`

Request:

```json
{
  "email": "user@mail.com",
  "password": "Password1"
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
  "password": "Password1",
  "full_name": "Eco Warrior",
  "city": "Almaty",
  "university": "Satbayev University",
  "faculty": "Computer Science"
}
```

Response can be either user object or login-like response. Android currently registers, then calls login.

### POST `/auth/token/refresh/`

Android calls this automatically after `401`.

Request:

```json
{
  "refresh": "jwt_refresh_token"
}
```

Response:

```json
{
  "access": "new_jwt_access_token",
  "refresh": "new_or_same_refresh_token"
}
```

`refresh` is optional on Android side. If backend returns only `access`, Android keeps the old refresh token.

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
  "total_recycled_kg": "31.5",
  "level": 8,
  "created_at": "2026-06-01T10:00:00Z",
  "updated_at": "2026-06-22T10:00:00Z"
}
```

## Common Pagination Format

List endpoints should return:

```json
{
  "count": 1,
  "next": null,
  "previous": null,
  "results": []
}
```

## Waste Categories

### GET `/waste-categories/`

Allowed `slug` values:

- `plastic`
- `paper`
- `glass`
- `batteries`
- `electronics`
- `organic`
- `metal`

Response item:

```json
{
  "id": "cat-plastic",
  "name": "Plastic",
  "slug": "plastic",
  "description": "Plastic bottles and packages",
  "points_per_kg": 35,
  "icon": "recycling",
  "color_hex": "#2F8F3A"
}
```

## Recycling Points

### GET `/recycling-points/?search=plastic`

Response item:

```json
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
```

### GET `/recycling-points/{id}/`

Response: same item.

## Submit Waste

Android supports JSON without photo and multipart with photo.

### POST `/submit-waste/` JSON

Request:

```json
{
  "category": "cat-plastic",
  "recycling_point": "point-1",
  "weight_kg": "5.0",
  "comment": "Five plastic bottles"
}
```

### POST `/submit-waste/` Multipart

Content type:

```http
multipart/form-data
```

Fields:

- `category`: category id
- `recycling_point`: point id
- `weight_kg`: string decimal
- `comment`: optional text
- `photo`: optional image file

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
  "photo": "https://example.com/submission.jpg",
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

## Recycling History

### GET `/waste-submissions/`

Response: paginated list of waste submission items.

Android uses this for profile recycling history and offline cache.

## Biotoilets

### GET `/biotoilets/`

Supported query filters:

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

Allowed status:

- `open`
- `unknown`
- `closed`
- `maintenance`

Allowed type:

- `free`
- `paid`

### GET `/biotoilets/{id}/`

Response: same item.

## Water Stations

### GET `/water-stations/`

Supported query filters:

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

Allowed water type:

- `free_drinking_water`
- `refill_station`
- `filtered_water`
- `water_dispenser`
- `bottled_water_vending_machine`

Allowed status:

- `available`
- `unknown`
- `temporarily_unavailable`
- `maintenance`

### GET `/water-stations/{id}/`

Response: same item.

## Eco Reports

### GET `/eco-reports/`

Supported query filters:

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
  "comments": [
    {
      "user_name": "Aibek",
      "comment": "Still there today.",
      "created_at": "2026-06-22T10:00:00Z"
    }
  ]
}
```

Allowed status:

- `submitted`
- `verified`
- `in_progress`
- `resolved`
- `rejected`

Allowed severity:

- `low`
- `medium`
- `high`

### GET `/eco-reports/{id}/`

Response: same item.

### POST `/eco-reports/` Multipart

Fields:

- `title`
- `address`
- `latitude`
- `longitude`
- `waste_description`
- `severity`
- `photo`: optional image file

Response: eco report item.

### POST `/eco-reports/{id}/photo/` Multipart

Fields:

- `photo`: image file
- `comment`: optional text

Response: updated eco report item.

### POST `/eco-reports/{id}/verify/`

Response: updated eco report item.

Recommendation:

- Give points for useful verification/photo updates through a ledger.
- Increment `verification_count`.
- Reject duplicate reports server-side when locations are very close.

## AI Waste Scanner

### POST `/ai-waste-scanner/analyze/` Form URL Encoded

Android can call demo mode:

```http
provider=demo
```

### POST `/ai-waste-scanner/analyze/` Multipart

Fields:

- `image`: image file
- `provider`: text, for example `android-camera`

Response:

```json
{
  "id": "scan-1",
  "image": "https://example.com/scan.jpg",
  "provider": "android-camera",
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

Recommendation:

- If real AI is not ready, return a fake but stable response based on image upload.
- Keep `confidence` from `0.0` to `1.0`.
- Return `predicted_category.slug` using the allowed waste slugs.

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

Allowed status:

- `daily`
- `weekly`
- `special`

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

Backend should return sorted users by points. Android still has fallback sorting.

## Eco Points Economy

Android level logic:

- `100 EcoPoints = 1 level`
- `max level = 10`
- `level = min(eco_points / 100, 10)`
- progress is `eco_points % 100`

Recommended backend ledger table:

| Field | Example |
| --- | --- |
| id | points-1 |
| user | user-1 |
| source | waste_submission |
| source_id | submission-1 |
| points | 175 |
| title | Plastic approved |
| created_at | ISO date |

Recommended sources:

- `waste_submission`
- `challenge_reward`
- `achievement_bonus`
- `eco_report_verify`
- `eco_report_photo`
- `water_station_verify`
- `biotoilet_report`

## Error Format

Recommended API error:

```json
{
  "detail": "Human readable error message",
  "code": "validation_error",
  "fields": {
    "email": ["This field is required."]
  }
}
```

Use HTTP codes correctly:

- `400`: validation
- `401`: expired or invalid access token
- `403`: no permission
- `404`: object not found
- `413`: image too large
- `415`: invalid image type
- `500`: server error

## Image Upload Rules

Recommended backend limits:

- Max image size: `5 MB`
- Allowed types: `image/jpeg`, `image/png`, `image/webp`
- Store original and optionally generate thumbnail.
- Return public or authenticated URL in `photo` / `image`.

## CORS And Local Testing

For Android physical phone:

- Django must run on `0.0.0.0:8000`
- Windows firewall must allow TCP `8000`
- Phone and laptop must be on same Wi-Fi
- Android debug base URL must use laptop LAN IP, not `127.0.0.1`

Example:

```powershell
python manage.py runserver 0.0.0.0:8000
```

## Production Checklist For Backend

- Use HTTPS in production.
- Enable JWT access + refresh token.
- Add `/auth/token/refresh/`.
- Support multipart upload endpoints listed above.
- Return stable IDs as strings.
- Return coordinates as strings or numbers; Android handles both when mapped through DTO strings.
- Keep list endpoints paginated.
- Add database indexes for:
  - user email
  - created_at fields
  - latitude/longitude or PostGIS location fields
  - status/severity filters
- Add admin moderation for waste submissions and eco reports.
- Add points ledger to prevent double-awarding.
- Add rate limiting for auth and image upload endpoints.

## Android Handoff Notes

Android already has:

- Offline cache fallback for map/profile/history.
- Unified error UI.
- Token refresh retry on `401`.
- Release config using secrets outside git.
- Unit tests for EcoRating, validators, UI state and ViewModels.

Backend can now connect endpoint by endpoint. Start with:

1. Auth login/register/me/refresh.
2. Waste categories.
3. Recycling points.
4. Waste submissions.
5. Map features: biotoilets, water stations, eco reports.
6. AI scanner multipart endpoint.
7. Points economy and leaderboard.
