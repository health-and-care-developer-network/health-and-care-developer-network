 <?php
/*
Template Name: Contributors
*/
?>

<?php get_header(); ?>
		

<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>
<div role="main" class="main">
	<div class="page_title">
		<div class="wrapper">
			<hgroup>
				<h1><?php the_title(); ?></h1>
				<h2>Individuals, companies &amp; organisations that have contributed to the Health Developer Network.</h2>
			</hgroup>
		</div>
	</div><!--end page title-->
	
	<div class="main_content cf">
		<?php include_once "breadcrumbs.php"; ?>

		<div class="wrapper cf container">
		<div class="content_wrap cf">
			<div class="half">
				<div class="contributor_list_l">
					<h2 class="contributor_titles">Individuals</h2>



					<?php if( get_field('individuals') ): ?>
					<?php while( has_sub_field('individuals') ): ?>
					<section class="contributor">
						<?php $image = wp_get_attachment_image_src(get_sub_field('contrib_image'), 'contributor_image'); ?>
						<img src="<?php echo $image[0]; ?>" alt="picture of a contributor">
						<h3><?php the_sub_field('contrib_name'); ?></h3>
						<ul class="blog_meta cf">
							<li><a  target="_blank" href="http://<?php the_sub_field('contib_web'); ?>"><?php the_sub_field('contib_web'); ?></a></li>
							<li><a href="mailto:<?php the_sub_field('contrib_email'); ?>"><?php the_sub_field('contrib_email'); ?></a></li>
						</ul>
						<p><?php the_sub_field('contrib_des'); ?></p>
					</section>
					<?php endwhile; ?>
					<?php endif; ?>
						
 

				</div><!--end controbutor_list-->
			</div><!--end half-->

			<div class="half">
				<div class="contributor_list_r">
					<h2 class="contributor_titles">Companies &amp; Organisations</h2>
					<?php if( get_field('organisations') ): ?>
					<?php while( has_sub_field('organisations') ): ?>
					<section class="contributor">
						<?php $image = wp_get_attachment_image_src(get_sub_field('contrib_image'), 'contributor_image'); ?>
						<img src="<?php echo $image[0]; ?>" alt="picture of a contributor">
						<h3><?php the_sub_field('contrib_name'); ?></h3>
						<ul class="blog_meta cf">
							<li><a  target="_blank" href="http://<?php the_sub_field('contib_web'); ?>"><?php the_sub_field('contib_web'); ?></a></li>
							<li><a href="mailto:<?php the_sub_field('contrib_email'); ?>"><?php the_sub_field('contrib_email'); ?></a></li>
						</ul>
						<p><?php the_sub_field('contrib_des'); ?></p>
					</section>
					<?php endwhile; ?>
					<?php endif; ?>
				</div><!--end controbutor_list-->
			</div><!--end half-->

		</div><!--end content wrap-->
	<?php endwhile; endif; ?>
				
			</div><!--end two thirds-->
			
		</div><!--end wrapper-->
	</div><!--end main_content-->
</div><!-- end main -->
		
<?php get_footer(); ?>
