<?php
/*-----------------------------------------
Add Parent class to tree nav items with children.
-----------------------------------------*/
function add_parent_class( $css_class, $page, $depth, $args )
{
    if ( ! empty( $args['has_children'] ) )
        $css_class[] = 'parent';
    return $css_class;
}
add_filter( 'page_css_class', 'add_parent_class', 10, 4 );

function custom_excerpt_length( $length ) {
	return 20;
}
add_filter( 'excerpt_length', 'custom_excerpt_length', 999 );

add_shortcode('wp_caption', 'fixed_img_caption_shortcode');
add_shortcode('caption', 'fixed_img_caption_shortcode');
function fixed_img_caption_shortcode($attr, $content = null) {
 
if ( ! isset( $attr['caption'] ) ) {
if ( preg_match( '#((?:<a [^>]+>\s*)?<img [^>]+>(?:\s*</a>)?)(.*)#is', $content, $matches ) ) {
$content = $matches[1];
$attr['caption'] = trim( $matches[2] );
}
}
 
$output = apply_filters('img_caption_shortcode', '', $attr, $content);
if ( $output != '' )
return $output;
 
extract(shortcode_atts(array(
'id' => '',
'align' => 'alignnone',
'width' => '',
'caption' => ''
), $attr));
 
if ( 1 > (int) $width || empty($caption) )
return $content;
 
if ( $id ) $id = 'id="' . esc_attr($id) . '" ';
 
return '<div ' . $id . 'class="wp-caption ' . esc_attr($align) . '" style="width: ' . $width . 'px">'
. do_shortcode( $content ) . '<p>' . $caption . '</p></div>';
}

// add_action('init', 'remove_admin_bar');

// function remove_admin_bar() {
// if (!current_user_can('administrator') && !is_admin()) {
//   show_admin_bar(false);
// }
// }

add_image_size( 'contributor_image', 215, 71 ); // Contributor image size

add_filter( 'mce_buttons_2', 'my_mce_buttons_2' );
function my_mce_buttons_2( $buttons ) {
    array_unshift( $buttons, 'styleselect' );
    return $buttons;
}

add_filter( 'tiny_mce_before_init', 'my_mce_before_init' );

function my_mce_before_init( $settings ) {

    $style_formats = array(
        array(
        	'title' => 'Grey Box',
        	'block' => 'div',
        	'classes' => 'grey_box',
        	'wrapper' => true
        ),
        array(
        	'title' => 'Green Box',
        	'block' => 'div',
        	'classes' => 'green_box',
        	'wrapper' => true
        ),
        array(
        	'title' => 'Red Box',
        	'block' => 'div',
        	'classes' => 'red_box',
        	'wrapper' => true
        ),
        array(
        	'title' => 'Blue Box',
        	'block' => 'div',
        	'classes' => 'blue_box',
        	'wrapper' => true
        ),
        array(
        	'title' => 'Red Button',
        	'selector' => 'a',
    		'classes' => 'btn_mce red_btn'
        ),
        array(
        	'title' => 'Aqua Green Button',
        	'selector' => 'a',
    		'classes' => 'btn_mce aquaGreen_btn'
        ),
        array(
        	'title' => 'Purple Button',
        	'selector' => 'a',
    		'classes' => 'btn_mce purple_btn'
        ),
        array(
        	'title' => 'Orange Button',
        	'selector' => 'a',
    		'classes' => 'btn_mce orange_btn'
        ),
    );

    $settings['style_formats'] = json_encode( $style_formats );

    return $settings;
}

add_action( 'admin_init', 'add_my_editor_style' );

function add_my_editor_style() {
	add_editor_style();
}


function wph_right_now_content_table_end() {
 $args = array(
  'public' => true ,
  '_builtin' => false
 );
 $output = 'object';
 $operator = 'and';
 $post_types = get_post_types( $args , $output , $operator );
 foreach( $post_types as $post_type ) {
  $num_posts = wp_count_posts( $post_type->name );
  $num = number_format_i18n( $num_posts->publish );
  $text = _n( $post_type->labels->singular_name, $post_type->labels->name , intval( $num_posts->publish ) );
  if ( current_user_can( 'edit_posts' ) ) {
   $num = "<a href='edit.php?post_type=$post_type->name'>$num</a>";
   $text = "<a href='edit.php?post_type=$post_type->name'>$text</a>";
  }
  echo '<tr><td class="first b b-' . $post_type->name . '">' . $num . '</td>';
  echo '<td class="t ' . $post_type->name . '">' . $text . '</td></tr>';
 }
 $taxonomies = get_taxonomies( $args , $output , $operator );
 foreach( $taxonomies as $taxonomy ) {
  $num_terms  = wp_count_terms( $taxonomy->name );
  $num = number_format_i18n( $num_terms );
  $text = _n( $taxonomy->labels->singular_name, $taxonomy->labels->name , intval( $num_terms ));
  if ( current_user_can( 'manage_categories' ) ) {
   $num = "<a href='edit-tags.php?taxonomy=$taxonomy->name'>$num</a>";
   $text = "<a href='edit-tags.php?taxonomy=$taxonomy->name'>$text</a>";
  }
  echo '<tr><td class="first b b-' . $taxonomy->name . '">' . $num . '</td>';
  echo '<td class="t ' . $taxonomy->name . '">' . $text . '</td></tr>';
 }
}
add_action( 'right_now_content_table_end' , 'wph_right_now_content_table_end' );

?>
