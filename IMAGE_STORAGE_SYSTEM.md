# Image Storage System

## Architecture

### Frontend (Gusto)
- **Storage Location**: `Gusto/src/images/`
- **Naming Convention**: `{timestamp}_{randomId}_{originalName}.jpg`
- **Resolution**: 800x600 (redimensionné par le backend)
- **Quality**: 75% compression

### Backend (Spring Boot)
- **Upload Endpoint**: `POST /api/plats/{id}/upload-image`
- **Storage Path**: `../Gusto/src/images` (relative to BackendProject)
- **Image Handling**: 
  - Redimensionne l'image à 800x600 avec Thumbnailator
  - Stocke juste le nom du fichier dans la BD
  - Supprime l'ancienne image automatiquement

### Database
- **Field**: `Plat.photoUrl`
- **Content**: Juste le nom du fichier UUID (ex: `550e8400-e29b-41d4-a716-446655440000.jpg`)

## Frontend Usage

### Display Images
```jsx
import { getImageDisplayUrl } from '../../utils/imageStorage';

// In component
<img src={getImageDisplayUrl(plat.photoUrl)} alt={plat.nom} />
```

### Upload Images
1. User selects image in form
2. FileReader creates immediate preview (data URL)
3. On form submit: file uploaded to `/api/plats/{platId}/upload-image`
4. Backend saves to `Gusto/src/images/`
5. Response contains filename for display

## How It Works

### File Flow
1. **Frontend**: User selects image → FileReader preview shown
2. **FormData**: File sent to backend with FormData
3. **Backend**: 
   - Validates file
   - Creates unique filename with timestamp
   - Resizes to 800x600 using Thumbnailator
   - Saves to `../Gusto/src/images/filename.jpg`
   - Stores filename in `Plat.photoUrl` field
4. **Display**: `getImageDisplayUrl()` constructs path as `/src/images/filename.jpg`

## Image URL Construction

### getImageDisplayUrl(photoUrl)
- Input: `550e8400-e29b-41d4-a716-446655440000.jpg`
- Output: `http://localhost:5173/src/images/550e8400-e29b-41d4-a716-446655440000.jpg`
- Works: ✓ During development (Vite serves src/)
- Works: ✓ In built app (images built into dist/)

## Categories Handling

Categories are also retrieved from the database:
- **Endpoint**: `GET /api/categories`
- **Display**: Category buttons with filtering
- **Category Type**: Stored as integer ID, converted to string for form state

## Usage Workflow

### Creating a Plat with Image
1. Fill form (nom, description, prix, category)
2. Select image from file input
3. Instant preview shown (via FileReader)
4. Click "Confirmer"
5. Backend saves plat (with empty photoUrl initially)
6. Image uploads to `Gusto/src/images/`
7. List refreshes with image displayed

### Editing a Plat
1. Click "Modifier" on existing plat
2. Form pre-fills with current data including `photoUrl`
3. Current image displays in form
4. Can select new image (old one deleted after save)
5. Save updates both plat metadata and image

## Troubleshooting

### Images not showing
- Check browser DevTools → Network tab
- Verify file exists in `Gusto/src/images/`
- Check console for image error messages
- Fallback: SVG placeholder with "Image non disponible"

### Upload fails
- Check file size (max 5MB)
- Check file format (jpg, png, gif, webp)
- Verify backend is running
- Check logs for error messages

### Path issues
- Backend: Uses `../Gusto/src/images` relative path
- Must run BackendProject from its own directory
- Frontend: Vite serves `/src/images` automatically in dev mode
