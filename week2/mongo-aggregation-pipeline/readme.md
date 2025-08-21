# MongoDB Commands and Operations Guide

This README contains practical MongoDB examples covering installation, authentication, CRUD operations, aggregation pipelines, and pagination.

## Table of Contents
- [Installation and Setup](#installation-and-setup)
- [Authentication](#authentication)
- [Sample Data](#sample-data)
- [Basic Queries](#basic-queries)
- [Delete Operations](#delete-operations)
- [Count Operations](#count-operations)
- [Advanced Queries](#advanced-queries)
- [Array Queries](#array-queries)
- [Complex String Operations](#complex-string-operations)
- [Update Operations with Aggregation Pipelines](#update-operations-with-aggregation-pipelines)
- [Pagination](#pagination)

## Installation and Setup

### Install MongoDB on macOS using Homebrew

```bash
# Add MongoDB Homebrew tap
brew tap mongodb/brew

# Update Homebrew
brew update

# Install MongoDB Community Edition 8.0
brew install mongodb-community@8.0

# Start MongoDB service
brew services start mongodb-community@8.0

# Connect to MongoDB shell
mongosh
```

## Authentication

### Enable Authentication

1. **Edit MongoDB configuration file:**
```bash
nano /opt/homebrew/etc/mongod.conf
```

2. **Add security configuration:**
```yaml
security:
  authorization: enabled
```

3. **Restart MongoDB service:**
```bash
brew services restart mongodb-community
```

4. **Connect with authentication:**
```bash
mongosh mongodb://rohit:rohitsingh@localhost:27017/admin
```

## Sample Data

### Insert Movie Documents

```javascript
db.movies.insertMany([
  {
    title: "Titanic",
    year: 1997,
    genres: ["Drama", "Romance"],
    rated: "PG-13",
    languages: ["English", "French", "German", "Swedish", "Italian", "Russian"],
    released: ISODate("1997-12-19T00:00:00.000Z"),
    awards: {
      wins: 127,
      nominations: 63,
      text: "Won 11 Oscars. Another 116 wins & 63 nominations."
    },
    cast: ["Leonardo DiCaprio", "Kate Winslet", "Billy Zane", "Kathy Bates"],
    directors: ["James Cameron"]
  },
  {
    title: "The Dark Knight",
    year: 2008,
    genres: ["Action", "Crime", "Drama"],
    rated: "PG-13",
    languages: ["English", "Mandarin"],
    released: ISODate("2008-07-18T00:00:00.000Z"),
    awards: {
      wins: 144,
      nominations: 106,
      text: "Won 2 Oscars. Another 142 wins & 106 nominations."
    },
    cast: ["Christian Bale", "Heath Ledger", "Aaron Eckhart", "Michael Caine"],
    directors: ["Christopher Nolan"]
  },
  {
    title: "Spirited Away",
    year: 2001,
    genres: ["Animation", "Adventure", "Family"],
    rated: "PG",
    languages: ["Japanese"],
    released: ISODate("2003-03-28T00:00:00.000Z"),
    awards: {
      wins: 52,
      nominations: 22,
      text: "Won 1 Oscar. Another 51 wins & 22 nominations."
    },
    cast: ["Rumi Hiiragi", "Miyu Irino", "Mari Natsuki", "Takashi Naitè"],
    directors: ["Hayao Miyazaki"]
  },
  {
    title: "Casablanca",
    genres: ["Drama", "Romance", "War"],
    rated: "PG",
    cast: ["Humphrey Bogart", "Ingrid Bergman", "Paul Henreid", "Claude Rains"],
    languages: ["English", "French", "German", "Italian"],
    released: ISODate("1943-01-23T00:00:00.000Z"),
    directors: ["Michael Curtiz"],
    awards: {
      wins: 9,
      nominations: 6,
      text: "Won 3 Oscars. Another 6 wins & 6 nominations."
    },
    lastupdated: "2015-09-04 00:22:54.600000000",
    year: 1942
  }
])
```

## Basic Queries

### Find All Documents
```javascript
db.movies.find({})
```

### Find by Director
```javascript
db.movies.find({ "directors": "Christopher Nolan" })
```

### Find by Date Range
```javascript
db.movies.find({ 
  released: { $lt: ISODate("2000-01-01") } 
})
```

## Delete Operations

### Delete Single Document
```javascript
db.movies.deleteOne({ 'title': 'Titanic' })
```

## Count Operations

### Count All Documents
```javascript
db.movies.countDocuments()
```

## Advanced Queries

### Query Nested Objects
```javascript
// Find movies with less than 15 award wins
db.movies.find({ "awards.wins": { $lt: 15 } })

// Multiple conditions on nested objects and arrays
db.movies.find({
  "awards.wins": { $gt: 15 }, 
  "directors": "Christopher Nolan"
})
```

### Exact Object Match
```javascript
// Note: This requires exact match of the entire object
db.movies.find({
  awards: {
    wins: 144,
    nominations: 106,
    text: "Won 2 Oscars. Another 142 wins & 106 nominations."
  }
})
```

## Array Queries

### Exact Array Match (Order Matters)
```javascript
// This requires exact order and elements
db.movies.find({
  cast: ["Christian Bale", "Heath Ledger"]
})
```

### Array Contains All Elements (Order Doesn't Matter)
```javascript
// Find documents where cast contains all specified actors
db.movies.find({
  cast: { 
    $all: ["Christian Bale", "Heath Ledger", "Michael Caine"] 
  }
})
```

## Complex String Operations

### Find Cast Members with Names Longer Than 10 Characters
```javascript
db.movies.find({
  $expr: {
    $anyElementTrue: {
      $map: {
        input: "$cast",
        as: "actor",
        in: { $gt: [{ $strLenCP: "$$actor" }, 10] }
      }
    }
  }
})
```

## Update Operations with Aggregation Pipelines

### Simple Field Update with Literal Values
```javascript
db.movies.updateOne(
  { title: "Casablanca" },
  [
    { $set: { 
        "awards.wins": { $literal: 11 }, 
        genres: "Drama" 
      } 
    }
  ]
)
```

### Add Default Fields to All Documents
```javascript
db.movies.updateMany({},
  [
    { 
      $replaceRoot: { 
        newRoot: { 
          $mergeObjects: [{ lastupdatedBy: "NA" }, "$$ROOT"] 
        } 
      } 
    },
    { $set: { lastupdatedBy: "Rohit" } }
  ]
)
```

### Add Random Rating Arrays
```javascript
db.movies.updateMany({},
  [
    {
      $replaceRoot: {
        newRoot: {
          $mergeObjects: [
            {
              rating: [
                { $floor: { $add: [{ $multiply: [{ $rand: {} }, 5] }, 1] } },
                { $floor: { $add: [{ $multiply: [{ $rand: {} }, 5] }, 1] } },
                { $floor: { $add: [{ $multiply: [{ $rand: {} }, 5] }, 1] } },
                { $floor: { $add: [{ $multiply: [{ $rand: {} }, 5] }, 1] } }
              ]
            },
            "$$ROOT"
          ]
        }
      }
    }
  ]
)
```

### Calculate Average Rating and Assign Grades
```javascript
db.movies.updateMany({},
  [
    { 
      $set: { 
        averageRating: { $trunc: [{ $avg: "$rating" }, 1] } 
      } 
    },
    { 
      $set: { 
        grade: { 
          $switch: {
            branches: [
              { case: { $gte: ["$averageRating", 5] }, then: "A" },
              { case: { $gte: ["$averageRating", 4] }, then: "B" },
              { case: { $gte: ["$averageRating", 3] }, then: "C" },
              { case: { $gte: ["$averageRating", 2] }, then: "D" }
            ],
            default: "F"
          } 
        } 
      } 
    }
  ]
)
```

### Concatenate Arrays
```javascript
db.movies.updateOne(
  { _id: ObjectId('68a6a7a4834fbf4ff7df2a91') },
  [
    { 
      $set: { 
        rating: { $concatArrays: ["$rating", [5, 5]] } 
      } 
    }
  ]
)
```

### Transform Array Elements with $map
```javascript
db.movies.updateMany({},
  [
    { 
      $addFields: { 
        "points": {
          $map: {
            input: "$rating",
            as: "rating",
            in: { $add: [{ $multiply: ["$$rating", 9/5] }, 32] }
          }
        } 
      } 
    }
  ]
)
```

### Conditional Update with Variables
```javascript
db.movies.updateOne(
  {
    $expr: { $eq: ["$grade", "$$existingGrade"] }
  },
  [
    {
      $set: { grade: "$$newGrade" }
    }
  ],
  {
    let: { existingGrade: "D", newGrade: "C" }
  }
)
```

### Grade Increment (Letter Grade Promotion)
```javascript
db.movies.updateMany({},
  [
    {
      $set: {
        grade: {
          $switch: {
            branches: [
              { case: { $eq: ["$grade", "F"] }, then: "D" },
              { case: { $eq: ["$grade", "D"] }, then: "C" },
              { case: { $eq: ["$grade", "C"] }, then: "B" },
              { case: { $eq: ["$grade", "B"] }, then: "A" }
            ],
            default: {
              $cond: {
                if: { $eq: ["$grade", "A"] },
                then: "A+",
                else: "$grade"
              }
            }
          }
        }
      }
    }
  ]
)
```

## Pagination

### Basic Pagination with Skip and Limit
```javascript
// Skip first 20 documents, return next 10
db.movies.find().skip(20).limit(10)

// With sorting
db.movies.find()
  .sort({ title: 1 })  // Sort by title ascending
  .skip(20)
  .limit(10)
```

### Cursor-Based Pagination (Recommended for Large Datasets)

#### Using _id as Cursor
```javascript
// First page
db.movies.find().sort({ _id: 1 }).limit(10)

// Next page (using last _id from previous result)
const lastId = ObjectId("last_id_from_previous_page");
db.movies.find({ _id: { $gt: lastId } })
  .sort({ _id: 1 })
  .limit(10)
```

#### Using Custom Field as Cursor
```javascript
// First page - sorted by year
db.movies.find().sort({ year: 1, _id: 1 }).limit(10)

// Next page
const lastYear = 2020;
const lastId = ObjectId("last_id_from_previous_page");
db.movies.find({
  $or: [
    { year: { $gt: lastYear } },
    { year: lastYear, _id: { $gt: lastId } }
  ]
}).sort({ year: 1, _id: 1 }).limit(10)
```

## Key Concepts

### System Variables
- **`$$ROOT`** - References the entire current document
- **`$$NOW`** - Current date and time
- **`$$literal`** - Treats expression as literal value

### Important Notes
- **Exact Object Match**: When querying nested objects, all fields must match exactly
- **Array Order**: Exact array matches require same order and elements
- **Performance**: Use cursor-based pagination for large datasets
- **Indexing**: Create appropriate indexes for your query patterns

### Aggregation Pipeline Stages for Updates
- **`$set`** - Add or update fields
- **`$addFields`** - Add new fields (alias for $set)
- **`$replaceRoot`** - Replace the entire document structure
- **`$unset`** - Remove fields
- **`$mergeObjects`** - Merge multiple objects

## Best Practices

1. **Always use sorting** with pagination for consistent results
2. **Create indexes** on frequently queried fields
3. **Use projection** to limit returned fields when possible
4. **Prefer cursor-based pagination** for large datasets
5. **Validate input** when using skip/limit pagination
6. **Use aggregation pipelines** for complex update operations