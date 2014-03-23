# clean output
rm -Rf output.avi

# récupérer les amplitudes max des deux micros
micro1_amp=$(sox micro1.wav -n stat -v 2>&1)
#micro2_amp=$(sox micro2.wav -n stat -v 2>&1)

# normalisation de chaque micro. Doit-on gérer une balance ?
sox -v $micro1_amp micro1.wav micro1_norm.wav
#sox -v $micro2_amp micro2.wav micro2_norm.wav





# Fusion des deux bandes son micro en une seule
#sox -m micro1_norm.wav micro2_norm.wav micro_all.wav

# suppression de la bande son de la video
#ffmpeg -map 0:0 -vcodec copy -i video.mp4 new_file.mp4

# découpe video post clap (reperage visuel)
# ffmpeg -ss 00:00:06 -i output.mp4 -async 1 cut.mp4

# decoupe son post clap (reperage manuel)
# sox micro1_norm.wav micro1_norm_trim.wav trim 00:04.500 -0


# merge de la bande son avec la vidéo
ffmpeg -i video.mp4 -i micro1_norm.wav -qscale 0 output.mp4

# clean temps
#rm -f micro1_norm.wav micro2_norm.wav micro_all.wav
